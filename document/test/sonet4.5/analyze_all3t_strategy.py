import csv
import io
import sys
from dataclasses import dataclass
from collections import defaultdict

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")

FILE_PATH = r"c:\Dev\github\pod_boatrace\document\test\result_3T.tsv"


@dataclass(frozen=True)
class Strategy:
    name: str
    top_n: int
    sort_key: str = "exp"
    min_exp: float | None = None
    max_exp: float | None = None
    min_prob: float | None = None
    max_rank: int | None = None
    grade_whitelist: set[str] | None = None
    alevel_whitelist: set[int] | None = None
    min_alevel: int | None = None
    max_wind: int | None = None


class Stats:
    def __init__(self) -> None:
        self.bet_sum = 0
        self.return_sum = 0
        self.bet_count = 0
        self.hit_count = 0
        self.race_with_bets = 0

        self.cum_profit = 0
        self.peak_profit = 0
        self.max_drawdown = 0
        self.current_nohit_race_streak = 0
        self.max_nohit_race_streak = 0

        self.month_bet = defaultdict(int)
        self.month_return = defaultdict(int)
        self.month_count = defaultdict(int)
        self.year_bet = defaultdict(int)
        self.year_return = defaultdict(int)
        self.year_count = defaultdict(int)

    def update_race(self, ymd: str, selected: list[tuple[int, int]]) -> None:
        if not selected:
            return

        self.race_with_bets += 1

        race_bet = 0
        race_ret = 0
        race_hits = 0
        for betamt, hitamt in selected:
            race_bet += betamt
            race_ret += hitamt
            if hitamt > 0:
                race_hits += 1

        self.bet_sum += race_bet
        self.return_sum += race_ret
        self.bet_count += len(selected)
        self.hit_count += race_hits

        ym = ymd[:6]
        yy = ymd[:4]
        self.month_bet[ym] += race_bet
        self.month_return[ym] += race_ret
        self.month_count[ym] += len(selected)
        self.year_bet[yy] += race_bet
        self.year_return[yy] += race_ret
        self.year_count[yy] += len(selected)

        race_profit = race_ret - race_bet
        self.cum_profit += race_profit
        if self.cum_profit > self.peak_profit:
            self.peak_profit = self.cum_profit
        dd = self.cum_profit - self.peak_profit
        if dd < self.max_drawdown:
            self.max_drawdown = dd

        if race_hits == 0:
            self.current_nohit_race_streak += 1
            if self.current_nohit_race_streak > self.max_nohit_race_streak:
                self.max_nohit_race_streak = self.current_nohit_race_streak
        else:
            self.current_nohit_race_streak = 0

    def roi_pct(self) -> float:
        if self.bet_sum <= 0:
            return float("nan")
        return (self.return_sum / self.bet_sum - 1.0) * 100.0

    def hit_rate_pct(self) -> float:
        if self.bet_count <= 0:
            return float("nan")
        return (self.hit_count / self.bet_count) * 100.0

    def profit(self) -> int:
        return self.return_sum - self.bet_sum

    def profitable_month_ratio(self) -> tuple[int, int]:
        months = sorted(self.month_bet.keys())
        if not months:
            return (0, 0)
        win = 0
        for m in months:
            bet = self.month_bet[m]
            ret = self.month_return[m]
            if bet > 0 and ret >= bet:
                win += 1
        return (win, len(months))


def _to_int(s: str) -> int:
    if not s:
        return 0
    try:
        return int(s)
    except ValueError:
        return int(float(s))


def _to_float(s: str) -> float:
    if not s:
        return 0.0
    return float(s)


def select_bets_for_strategy(
    bets_sorted: list[tuple[float, float, int, int, int]],
    strat: Strategy,
    grade: str,
    alevelcount: int,
    wind: int,
    can_break_on_min_exp: bool,
) -> list[tuple[int, int]]:
    if strat.grade_whitelist is not None and grade not in strat.grade_whitelist:
        return []

    if strat.alevel_whitelist is not None and alevelcount not in strat.alevel_whitelist:
        return []

    if strat.min_alevel is not None and alevelcount < strat.min_alevel:
        return []

    if strat.max_wind is not None and wind > strat.max_wind:
        return []

    selected: list[tuple[int, int]] = []
    for exp, prob, rank, betamt, hitamt in bets_sorted:
        if strat.min_exp is not None and exp < strat.min_exp:
            if can_break_on_min_exp:
                break
            continue
        if strat.max_exp is not None and exp > strat.max_exp:
            continue
        if strat.min_prob is not None and prob < strat.min_prob:
            continue
        if strat.max_rank is not None and rank > strat.max_rank:
            continue

        selected.append((betamt, hitamt))
        if len(selected) >= strat.top_n:
            break

    return selected


def main() -> None:
    grade_big = {"SG", "G1", "G2", "G3"}
    grade_g1g3 = {"G1", "G3"}

    strategies = [
        Strategy("top1_ev", top_n=1),
        Strategy("top1_ev_ge_1.1", top_n=1, min_exp=1.1),
        Strategy("top1_ev_ge_1.2", top_n=1, min_exp=1.2),
        Strategy("top1_ev_ge_1.3", top_n=1, min_exp=1.3),
        Strategy("top1_ev_1.2_1.8", top_n=1, min_exp=1.2, max_exp=1.8),
        Strategy("top1_ev_1.3_1.5", top_n=1, min_exp=1.3, max_exp=1.5),
        Strategy("top1_ev_1.3_1.5_prob_0.05", top_n=1, min_exp=1.3, max_exp=1.5, min_prob=0.05),
        Strategy("top1_ev_1.3_1.5_prob_0.05_rank_5", top_n=1, min_exp=1.3, max_exp=1.5, min_prob=0.05, max_rank=5),
        Strategy("top1_ev_1.3_1.5_prob_0.05_rank_3", top_n=1, min_exp=1.3, max_exp=1.5, min_prob=0.05, max_rank=3),
        Strategy(
            "top1_ev_1.3_1.5_prob_0.05_rank_5_grade_g1g3",
            top_n=1,
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
        ),
        Strategy(
            "top1_ev_1.3_1.5_prob_0.05_rank_5_grade_g1g3_wind2",
            top_n=1,
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
            max_wind=2,
        ),
        Strategy(
            "top1_score_1.3_1.5_prob_0.05_rank_5_grade_g1g3_wind2",
            top_n=1,
            sort_key="score",
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
            max_wind=2,
        ),
        Strategy(
            "top1_ev_1.3_1.5_prob_0.05_rank_5_grade_g1g3_wind1",
            top_n=1,
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
            max_wind=1,
        ),
        Strategy(
            "top1_score_1.3_1.5_prob_0.05_rank_5_grade_g1g3_wind1",
            top_n=1,
            sort_key="score",
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
            max_wind=1,
        ),
        Strategy(
            "top1_score_1.3_1.5_prob_0.05_rank_5_grade_g1g3",
            top_n=1,
            sort_key="score",
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
        ),
        Strategy(
            "top1_ev_1.3_1.5_prob_0.06_rank_5_grade_g1g3",
            top_n=1,
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.06,
            max_rank=5,
            grade_whitelist=grade_g1g3,
        ),
        Strategy(
            "top1_ev_1.3_1.5_prob_0.07_rank_5_grade_g1g3",
            top_n=1,
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.07,
            max_rank=5,
            grade_whitelist=grade_g1g3,
        ),
        Strategy(
            "top1_ev_1.35_1.5_prob_0.05_rank_5_grade_g1g3",
            top_n=1,
            min_exp=1.35,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
        ),
        Strategy(
            "top1_ev_1.3_1.45_prob_0.05_rank_5_grade_g1g3",
            top_n=1,
            min_exp=1.3,
            max_exp=1.45,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
        ),
        Strategy(
            "top1_ev_1.4_1.5_prob_0.05_rank_5_grade_g1g3",
            top_n=1,
            min_exp=1.4,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
        ),
        Strategy(
            "top1_ev_1.3_1.5_prob_0.05_rank_5_grade_g1g3_alevel5",
            top_n=1,
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
            alevel_whitelist={5},
        ),
        Strategy(
            "top1_ev_1.3_1.5_prob_0.05_rank_5_grade_g1g3_alevel6",
            top_n=1,
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
            alevel_whitelist={6},
        ),
        Strategy(
            "top1_ev_1.3_1.5_prob_0.05_rank_5_grade_big",
            top_n=1,
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_big,
        ),
        Strategy(
            "top1_score_1.3_1.5_prob_0.05_rank_5_grade_big",
            top_n=1,
            sort_key="score",
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_big,
        ),
        Strategy("top2_ev_1.3_1.5_prob_0.05_rank_5", top_n=2, min_exp=1.3, max_exp=1.5, min_prob=0.05, max_rank=5),
        Strategy("top3_ev_1.3_1.5_prob_0.05_rank_5", top_n=3, min_exp=1.3, max_exp=1.5, min_prob=0.05, max_rank=5),
    ]

    stats = {s.name: Stats() for s in strategies}

    with open(FILE_PATH, "r", encoding="utf-8", newline="") as f:
        reader = csv.reader(f, delimiter="\t")
        header = next(reader)
        col = {name: i for i, name in enumerate(header)}

        idx_ymd = col["ymd"]
        idx_jyocd = col["jyocd"]
        idx_raceno = col["raceno"]
        idx_grade = col["grade"]
        idx_wind = col["wind"]
        idx_expect = col["expect_bor"]
        idx_prob = col["probability"]
        idx_rank = col["bet_oddsrank"]
        idx_alevel = col["alevelcount"]
        idx_betamt = col["betamt"]
        idx_hitamt = col["hitamt"]

        current_key = None
        current_ymd = ""
        current_grade = ""
        current_alevel = 0
        current_wind = 0
        race_bets: list[tuple[float, float, int, int, int]] = []

        years_seen: set[str] = set()

        total_rows = 0
        race_count = 0
        min_bpr = None
        max_bpr = 0

        def flush_race() -> None:
            nonlocal race_count, min_bpr, max_bpr, race_bets
            if current_key is None:
                return

            race_count += 1
            bpr = len(race_bets)
            if min_bpr is None:
                min_bpr = bpr
            else:
                min_bpr = min(min_bpr, bpr)
            max_bpr = max(max_bpr, bpr)

            years_seen.add(current_ymd[:4])

            bets_by_exp = sorted(race_bets, key=lambda x: x[0], reverse=True)
            bets_by_score = sorted(race_bets, key=lambda x: x[0] * x[1], reverse=True)

            for s in strategies:
                if s.sort_key == "score":
                    selected = select_bets_for_strategy(
                        bets_by_score,
                        s,
                        current_grade,
                        current_alevel,
                        current_wind,
                        False,
                    )
                else:
                    selected = select_bets_for_strategy(
                        bets_by_exp,
                        s,
                        current_grade,
                        current_alevel,
                        current_wind,
                        True,
                    )
                stats[s.name].update_race(current_ymd, selected)

            race_bets = []

        for row in reader:
            total_rows += 1
            ymd = row[idx_ymd]
            key = (ymd, row[idx_jyocd], row[idx_raceno])

            if current_key is None:
                current_key = key
                current_ymd = ymd
                current_grade = row[idx_grade]
                current_alevel = _to_int(row[idx_alevel])
                current_wind = _to_int(row[idx_wind])
            elif key != current_key:
                flush_race()
                current_key = key
                current_ymd = ymd
                current_grade = row[idx_grade]
                current_alevel = _to_int(row[idx_alevel])
                current_wind = _to_int(row[idx_wind])

            exp = _to_float(row[idx_expect])
            prob = _to_float(row[idx_prob])
            rank = _to_int(row[idx_rank])
            betamt = _to_int(row[idx_betamt])
            hitamt = _to_int(row[idx_hitamt])
            race_bets.append((exp, prob, rank, betamt, hitamt))

        flush_race()

    print("=" * 80)
    print("All 3T Strategy Backtest (per-race top-N selection)")
    print("=" * 80)
    print(f"file: {FILE_PATH}")
    print(f"rows: {total_rows:,}  races: {race_count:,}")
    print(f"bets_per_race: min={min_bpr} max={max_bpr}")

    rows_out = []
    for s in strategies:
        st = stats[s.name]
        pm_win, pm_total = st.profitable_month_ratio()
        rows_out.append(
            (
                st.roi_pct(),
                s.name,
                st.bet_count,
                st.race_with_bets,
                st.hit_rate_pct(),
                st.profit(),
                st.max_drawdown,
                f"{pm_win}/{pm_total}",
            )
        )

    rows_out.sort(key=lambda x: x[0], reverse=True)

    print("\n=== Summary (sorted by ROI) ===")
    print("ROI%\tstrategy\tbets\traces\thit%\tprofit\tmaxDD\tprofitableMonths")
    for roi, name, bets, races, hit, prof, mdd, pm in rows_out:
        print(f"{roi:.2f}\t{name}\t{bets}\t{races}\t{hit:.2f}\t{prof}\t{mdd}\t{pm}")

    print("\n=== Yearly ROI (top 10 strategies) ===")
    for roi, name, *_ in rows_out[:10]:
        st = stats[name]
        years = sorted(st.year_bet.keys())
        parts = []
        for y in years:
            bet = st.year_bet[y]
            ret = st.year_return[y]
            cnt = st.year_count.get(y, 0)
            if bet <= 0:
                continue
            parts.append(f"{y}:{(ret / bet - 1) * 100:.2f}%({cnt})")
        print(f"{name}: " + " ".join(parts))

    print("\n=== Robustness (maximize worst-year ROI) ===")
    robust = []
    for s in strategies:
        st = stats[s.name]
        if st.bet_count <= 0:
            continue

        year_rois = []
        for y in years_seen:
            bet = st.year_bet.get(y, 0)
            cnt = st.year_count.get(y, 0)
            if bet <= 0 or cnt <= 0:
                year_rois.append(float("-inf"))
            else:
                year_rois.append((st.year_return[y] / bet - 1) * 100)

        worst = min(year_rois) if year_rois else float("-inf")
        robust.append((worst, st.roi_pct(), s.name, st.bet_count))

    robust.sort(key=lambda x: (x[0], x[1]), reverse=True)
    print("worstYearROI%\toverallROI%\tstrategy\tbets")
    for worst, overall, name, bets in robust[:15]:
        print(f"{worst:.2f}\t{overall:.2f}\t{name}\t{bets}")


if __name__ == "__main__":
    main()
