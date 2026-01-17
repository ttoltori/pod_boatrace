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
class Params:
    sort_key: str
    min_exp: float
    max_exp: float
    min_prob: float
    max_rank: int
    max_wind: float | None


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

    def update_race(self, ymd: str, betamt: int, hitamt: int) -> None:
        self.race_with_bets += 1

        self.bet_sum += betamt
        self.return_sum += hitamt
        self.bet_count += 1
        if hitamt > 0:
            self.hit_count += 1

        ym = ymd[:6]
        yy = ymd[:4]
        self.month_bet[ym] += betamt
        self.month_return[ym] += hitamt
        self.month_count[ym] += 1
        self.year_bet[yy] += betamt
        self.year_return[yy] += hitamt
        self.year_count[yy] += 1

        race_profit = hitamt - betamt
        self.cum_profit += race_profit
        if self.cum_profit > self.peak_profit:
            self.peak_profit = self.cum_profit
        dd = self.cum_profit - self.peak_profit
        if dd < self.max_drawdown:
            self.max_drawdown = dd

        if hitamt <= 0:
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

    def min_year_bets(self, years_all: set[str]) -> int:
        if not years_all:
            return 0
        return min(self.year_count.get(y, 0) for y in years_all)


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


def build_param_grid() -> list[Params]:
    exp_ranges = [
        (1.30, 1.50),
        (1.30, 1.45),
        (1.35, 1.50),
        (1.35, 1.45),
        (1.40, 1.50),
        (1.25, 1.50),
    ]
    min_probs = [0.04, 0.05, 0.06, 0.07]
    max_ranks = [3, 5, 7]
    max_winds: list[float | None] = [None, 1.0, 2.0, 3.0]
    sort_keys = ["exp", "score"]

    grid: list[Params] = []
    for sort_key in sort_keys:
        for min_exp, max_exp in exp_ranges:
            for min_prob in min_probs:
                for max_rank in max_ranks:
                    for max_wind in max_winds:
                        grid.append(
                            Params(
                                sort_key=sort_key,
                                min_exp=min_exp,
                                max_exp=max_exp,
                                min_prob=min_prob,
                                max_rank=max_rank,
                                max_wind=max_wind,
                            )
                        )
    return grid


def main() -> None:
    grade_g1g3 = {"G1", "G3"}

    params_list = build_param_grid()
    stats = {p: Stats() for p in params_list}

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
        idx_betamt = col["betamt"]
        idx_hitamt = col["hitamt"]

        current_key = None
        current_ymd = ""
        current_grade = ""
        current_wind = 0.0
        keep_race = False
        bets: list[tuple[float, float, int, int, int]] = []

        years_seen: set[str] = set()
        total_rows = 0
        race_count = 0
        kept_race_count = 0

        def flush() -> None:
            nonlocal race_count, kept_race_count, bets
            if current_key is None:
                return

            race_count += 1
            if not keep_race or not bets:
                bets = []
                return

            kept_race_count += 1
            years_seen.add(current_ymd[:4])

            bets_by_exp = sorted(bets, key=lambda x: x[0], reverse=True)
            bets_by_score = sorted(bets, key=lambda x: x[0] * x[1], reverse=True)

            wind = current_wind
            ymd = current_ymd

            for p in params_list:
                if p.max_wind is not None and wind > p.max_wind:
                    continue

                selected = None
                if p.sort_key == "score":
                    for exp, prob, rank, betamt, hitamt in bets_by_score:
                        if exp < p.min_exp:
                            break
                        if exp > p.max_exp:
                            continue
                        if prob < p.min_prob:
                            continue
                        if rank > p.max_rank:
                            continue
                        selected = (betamt, hitamt)
                        break
                else:
                    for exp, prob, rank, betamt, hitamt in bets_by_exp:
                        if exp < p.min_exp:
                            break
                        if exp > p.max_exp:
                            continue
                        if prob < p.min_prob:
                            continue
                        if rank > p.max_rank:
                            continue
                        selected = (betamt, hitamt)
                        break

                if selected is None:
                    continue
                betamt, hitamt = selected
                stats[p].update_race(ymd, betamt, hitamt)

            bets = []

        for row in reader:
            total_rows += 1
            ymd = row[idx_ymd]
            key = (ymd, row[idx_jyocd], row[idx_raceno])

            if current_key is None:
                current_key = key
                current_ymd = ymd
                current_grade = row[idx_grade]
                current_wind = _to_float(row[idx_wind])
                keep_race = current_grade in grade_g1g3
            elif key != current_key:
                flush()
                current_key = key
                current_ymd = ymd
                current_grade = row[idx_grade]
                current_wind = _to_float(row[idx_wind])
                keep_race = current_grade in grade_g1g3

            if not keep_race:
                continue

            exp = _to_float(row[idx_expect])
            prob = _to_float(row[idx_prob])
            rank = _to_int(row[idx_rank])
            betamt = _to_int(row[idx_betamt])
            hitamt = _to_int(row[idx_hitamt])
            bets.append((exp, prob, rank, betamt, hitamt))

        flush()

    years_sorted = sorted(years_seen)

    rows_out = []
    for p in params_list:
        st = stats[p]
        if st.bet_count <= 0:
            continue
        pm_win, pm_total = st.profitable_month_ratio()
        rows_out.append(
            (
                st.worst_year_roi_pct(years_seen),
                st.roi_pct(),
                st.bet_count,
                st.min_year_bets(years_seen),
                st.hit_rate_pct(),
                st.profit(),
                st.max_drawdown,
                st.max_nohit_race_streak,
                pm_win,
                pm_total,
                p,
            )
        )

    rows_out.sort(key=lambda x: (x[0], x[1]), reverse=True)

    print("=" * 80)
    print("Grid Search (G1/G3 only, per-race top1)")
    print("=" * 80)
    print(f"file: {FILE_PATH}")
    print(f"rows: {total_rows:,} races: {race_count:,} kept_races(G1/G3): {kept_race_count:,}")
    print(f"years: {', '.join(years_sorted)}")
    print(f"strategies: {len(params_list):,}")

    def fmt_wind(w: float | None) -> str:
        return "none" if w is None else str(int(w))

    print("\n=== Top 30 by (worstYearROI, overallROI) with minYearBets>=10 ===")
    print("worstY%\toverall%\tbets\tminYbets\thit%\tprofit\tmaxDD\tmaxNoHit\tpm\tparams")
    shown = 0
    for worst, overall, bets, min_ybets, hit, prof, mdd, streak, pm_win, pm_total, p in rows_out:
        if min_ybets < 10:
            continue
        if overall <= 0:
            continue
        shown += 1
        params_str = (
            f"sort={p.sort_key} exp={p.min_exp:.2f}-{p.max_exp:.2f} "
            f"prob>={p.min_prob:.2f} rank<={p.max_rank} wind<={fmt_wind(p.max_wind)}"
        )
        print(
            f"{worst:.2f}\t{overall:.2f}\t{bets}\t{min_ybets}\t{hit:.2f}\t{prof}\t{mdd}\t{streak}\t{pm_win}/{pm_total}\t{params_str}"
        )
        if shown >= 30:
            break

    print("\n=== Top 20 by profitable-month ratio (overallROI>0, bets>=30) ===")
    ratio_rows = []
    for worst, overall, bets, min_ybets, hit, prof, mdd, streak, pm_win, pm_total, p in rows_out:
        if bets < 30:
            continue
        if overall <= 0:
            continue
        ratio = pm_win / pm_total if pm_total > 0 else 0.0
        ratio_rows.append((ratio, worst, overall, bets, min_ybets, hit, prof, mdd, streak, pm_win, pm_total, p))

    ratio_rows.sort(key=lambda x: (x[0], x[2], x[1]), reverse=True)

    print("pmRatio\tworstY%\toverall%\tbets\tminYbets\thit%\tprofit\tmaxDD\tmaxNoHit\tparams")
    for ratio, worst, overall, bets, min_ybets, hit, prof, mdd, streak, pm_win, pm_total, p in ratio_rows[:20]:
        params_str = (
            f"sort={p.sort_key} exp={p.min_exp:.2f}-{p.max_exp:.2f} "
            f"prob>={p.min_prob:.2f} rank<={p.max_rank} wind<={fmt_wind(p.max_wind)}"
        )
        print(
            f"{pm_win}/{pm_total}({ratio:.2f})\t{worst:.2f}\t{overall:.2f}\t{bets}\t{min_ybets}\t{hit:.2f}\t{prof}\t{mdd}\t{streak}\t{params_str}"
        )

    out_path = BASE_DIR / "grid_search_g1g3_results.tsv"
    with open(out_path, "w", encoding="utf-8", newline="") as out_f:
        w = csv.writer(out_f, delimiter="\t", lineterminator="\n")
        w.writerow(
            [
                "worst_year_roi_pct",
                "overall_roi_pct",
                "bets",
                "min_year_bets",
                "hit_rate_pct",
                "profit",
                "max_drawdown",
                "max_nohit_streak",
                "profitable_months_win",
                "profitable_months_total",
                "sort_key",
                "min_exp",
                "max_exp",
                "min_prob",
                "max_rank",
                "max_wind",
            ]
        )

        for worst, overall, bets, min_ybets, hit, prof, mdd, streak, pm_win, pm_total, p in rows_out:
            w.writerow(
                [
                    f"{worst:.6f}",
                    f"{overall:.6f}",
                    bets,
                    min_ybets,
                    f"{hit:.6f}",
                    prof,
                    mdd,
                    streak,
                    pm_win,
                    pm_total,
                    p.sort_key,
                    f"{p.min_exp:.6f}",
                    f"{p.max_exp:.6f}",
                    f"{p.min_prob:.6f}",
                    p.max_rank,
                    "" if p.max_wind is None else f"{p.max_wind:.6f}",
                ]
            )

    print(f"\nwritten: {out_path}")


if __name__ == "__main__":
    main()
