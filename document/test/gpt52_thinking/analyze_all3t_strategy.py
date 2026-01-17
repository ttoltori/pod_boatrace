import csv
import io
import sys
from dataclasses import dataclass
from collections import defaultdict
from pathlib import Path

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")

BASE_DIR = Path(__file__).resolve().parent
FILE_PATH = BASE_DIR / "result_3T.tsv"


@dataclass(frozen=True)
class Strategy:
    name: str
    top_n: int
    sort_key: str = "exp"  # exp or score
    min_exp: float | None = None
    max_exp: float | None = None
    min_prob: float | None = None
    max_rank: int | None = None
    grade_whitelist: set[str] | None = None
    alevel_whitelist: set[int] | None = None
    min_alevel: int | None = None
    max_wind: float | None = None
    fixedentrance_whitelist: set[str] | None = None


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

    def worst_year_roi_pct(self, years_all: set[str]) -> float:
        if not years_all:
            return float("nan")
        worst = float("inf")
        for y in years_all:
            bet = self.year_bet.get(y, 0)
            ret = self.year_return.get(y, 0)
            if bet <= 0:
                roi = float("-inf")
            else:
                roi = (ret / bet - 1.0) * 100.0
            if roi < worst:
                worst = roi
        return worst


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
    wind: float,
    fixedentrance: str,
    can_break_on_min_exp: bool,
) -> list[tuple[int, int]]:
    if strat.grade_whitelist is not None and grade not in strat.grade_whitelist:
        return []

    if strat.fixedentrance_whitelist is not None and fixedentrance not in strat.fixedentrance_whitelist:
        return []

    if strat.alevel_whitelist is not None and alevelcount not in strat.alevel_whitelist:
        return []

    if strat.min_alevel is not None and alevelcount < strat.min_alevel:
        return []

    if strat.max_wind is not None and wind > strat.max_wind:
        return []

    min_exp = strat.min_exp
    max_exp = strat.max_exp
    min_prob = strat.min_prob
    max_rank = strat.max_rank

    selected: list[tuple[int, int]] = []
    for exp, prob, rank, betamt, hitamt in bets_sorted:
        if min_exp is not None and exp < min_exp:
            if can_break_on_min_exp:
                break
            continue
        if max_exp is not None and exp > max_exp:
            continue
        if min_prob is not None and prob < min_prob:
            continue
        if max_rank is not None and rank > max_rank:
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
        Strategy("top1_score", top_n=1, sort_key="score"),
        Strategy("top1_ev_ge_1.1", top_n=1, min_exp=1.1),
        Strategy("top1_ev_ge_1.2", top_n=1, min_exp=1.2),
        Strategy("top1_ev_ge_1.3", top_n=1, min_exp=1.3),
        Strategy("top1_ev_ge_1.35", top_n=1, min_exp=1.35),
        Strategy("top1_ev_ge_1.4", top_n=1, min_exp=1.4),
        Strategy("top1_ev_1.3_1.5", top_n=1, min_exp=1.3, max_exp=1.5),
        Strategy("top1_ev_1.3_1.6", top_n=1, min_exp=1.3, max_exp=1.6),
        Strategy("top1_ev_1.3_1.45", top_n=1, min_exp=1.3, max_exp=1.45),
        Strategy("top1_ev_1.35_1.5", top_n=1, min_exp=1.35, max_exp=1.5),
        Strategy("top1_ev_1.4_1.5", top_n=1, min_exp=1.4, max_exp=1.5),
        Strategy("top1_ev_1.3_1.5_prob_0.04", top_n=1, min_exp=1.3, max_exp=1.5, min_prob=0.04),
        Strategy("top1_ev_1.3_1.5_prob_0.05", top_n=1, min_exp=1.3, max_exp=1.5, min_prob=0.05),
        Strategy("top1_ev_1.3_1.5_prob_0.06", top_n=1, min_exp=1.3, max_exp=1.5, min_prob=0.06),
        Strategy("top1_ev_1.3_1.5_prob_0.05_rank_3", top_n=1, min_exp=1.3, max_exp=1.5, min_prob=0.05, max_rank=3),
        Strategy("top1_ev_1.3_1.5_prob_0.05_rank_5", top_n=1, min_exp=1.3, max_exp=1.5, min_prob=0.05, max_rank=5),
        Strategy("top1_ev_1.3_1.5_prob_0.05_rank_7", top_n=1, min_exp=1.3, max_exp=1.5, min_prob=0.05, max_rank=7),
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
            "top1_ev_1.3_1.5_prob_0.05_rank_5_grade_big",
            top_n=1,
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_big,
        ),
        Strategy(
            "top1_ev_1.3_1.5_prob_0.05_rank_5_grade_g1g3_wind2",
            top_n=1,
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
            max_wind=2.0,
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
            max_wind=2.0,
        ),
        Strategy(
            "top1_ev_1.3_1.5_prob_0.05_rank_5_grade_g1g3_fixed",
            top_n=1,
            min_exp=1.3,
            max_exp=1.5,
            min_prob=0.05,
            max_rank=5,
            grade_whitelist=grade_g1g3,
            fixedentrance_whitelist={"Y"},
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
        Strategy("top2_ev_1.3_1.5_prob_0.05_rank_5", top_n=2, min_exp=1.3, max_exp=1.5, min_prob=0.05, max_rank=5),
        Strategy("top3_ev_1.3_1.5_prob_0.05_rank_5", top_n=3, min_exp=1.3, max_exp=1.5, min_prob=0.05, max_rank=5),
    ]

    stats = {s.name: Stats() for s in strategies}

    baseline_bet_sum = 0
    baseline_return_sum = 0
    baseline_bet_count = 0
    baseline_hit_count = 0

    with open(FILE_PATH, "r", encoding="utf-8", newline="") as f:
        reader = csv.reader(f, delimiter="\t")
        header = next(reader)
        col = {name: i for i, name in enumerate(header)}

        idx_ymd = col["ymd"]
        idx_jyocd = col["jyocd"]
        idx_raceno = col["raceno"]
        idx_grade = col["grade"]
        idx_wind = col["wind"]
        idx_fixed = col["fixedentrance"]
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
        current_wind = 0.0
        current_fixed = ""
        race_bets: list[tuple[float, float, int, int, int]] = []

        years_seen: set[str] = set()
        months_seen: set[str] = set()

        total_rows = 0
        race_count = 0
        min_bpr = None
        max_bpr = 0
        min_ymd = None
        max_ymd = None

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
            months_seen.add(current_ymd[:6])

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
                        current_fixed,
                        False,
                    )
                else:
                    selected = select_bets_for_strategy(
                        bets_by_exp,
                        s,
                        current_grade,
                        current_alevel,
                        current_wind,
                        current_fixed,
                        True,
                    )
                stats[s.name].update_race(current_ymd, selected)

            race_bets = []

        for row in reader:
            total_rows += 1

            ymd = row[idx_ymd]
            if min_ymd is None or ymd < min_ymd:
                min_ymd = ymd
            if max_ymd is None or ymd > max_ymd:
                max_ymd = ymd

            key = (ymd, row[idx_jyocd], row[idx_raceno])

            if current_key is None:
                current_key = key
                current_ymd = ymd
                current_grade = row[idx_grade]
                current_alevel = _to_int(row[idx_alevel])
                current_wind = _to_float(row[idx_wind])
                current_fixed = row[idx_fixed]
            elif key != current_key:
                flush_race()
                current_key = key
                current_ymd = ymd
                current_grade = row[idx_grade]
                current_alevel = _to_int(row[idx_alevel])
                current_wind = _to_float(row[idx_wind])
                current_fixed = row[idx_fixed]

            betamt = _to_int(row[idx_betamt])
            hitamt = _to_int(row[idx_hitamt])
            baseline_bet_sum += betamt
            baseline_return_sum += hitamt
            baseline_bet_count += 1
            if hitamt > 0:
                baseline_hit_count += 1

            exp = _to_float(row[idx_expect])
            prob = _to_float(row[idx_prob])
            rank = _to_int(row[idx_rank])
            race_bets.append((exp, prob, rank, betamt, hitamt))

        flush_race()

    baseline_roi = (baseline_return_sum / baseline_bet_sum - 1.0) * 100.0 if baseline_bet_sum > 0 else float("nan")
    baseline_hitrate = (baseline_hit_count / baseline_bet_count) * 100.0 if baseline_bet_count > 0 else float("nan")

    print("=" * 80)
    print("All 3T Strategy Backtest (per-race top-N selection)")
    print("=" * 80)
    print(f"file: {FILE_PATH}")
    print(f"period: {min_ymd} - {max_ymd}  months={len(months_seen)} years={len(years_seen)}")
    print(f"rows: {total_rows:,}  races: {race_count:,}")
    print(f"bets_per_race: min={min_bpr} max={max_bpr}")
    print("\n=== Baseline (bet ALL rows) ===")
    print(f"bet_sum={baseline_bet_sum:,} return_sum={baseline_return_sum:,} profit={baseline_return_sum - baseline_bet_sum:,} ROI%={baseline_roi:.2f} hit%={baseline_hitrate:.2f}")

    rows_out = []
    for s in strategies:
        st = stats[s.name]
        pm_win, pm_total = st.profitable_month_ratio()
        rows_out.append(
            (
                st.roi_pct(),
                st.worst_year_roi_pct(years_seen),
                s.name,
                st.bet_count,
                st.race_with_bets,
                st.hit_rate_pct(),
                st.profit(),
                st.max_drawdown,
                st.max_nohit_race_streak,
                f"{pm_win}/{pm_total}",
            )
        )

    rows_out.sort(key=lambda x: x[0], reverse=True)

    print("\n=== Summary (sorted by ROI) ===")
    print("ROI%\tworstYearROI%\tstrategy\tbets\traces\thit%\tprofit\tmaxDD\tmaxNoHitStreak\tprofitableMonths")
    for roi, worst, name, bets, races, hit, prof, mdd, streak, pm in rows_out:
        print(f"{roi:.2f}\t{worst:.2f}\t{name}\t{bets}\t{races}\t{hit:.2f}\t{prof}\t{mdd}\t{streak}\t{pm}")

    print("\n=== Yearly ROI (top 10 by overall ROI) ===")
    for _, _, name, *_ in rows_out[:10]:
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


if __name__ == "__main__":
    main()
