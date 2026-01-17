import csv
import io
import sys
from collections import defaultdict
from dataclasses import dataclass
from pathlib import Path

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")

BASE_DIR = Path(__file__).resolve().parent
FILE_PATH = BASE_DIR / "result_3T.tsv"


@dataclass(frozen=True)
class Strategy:
    name: str
    grade_whitelist: set[str]
    sort_key: str
    min_exp: float
    max_exp: float
    min_prob: float
    max_rank: int
    max_wind: float


class Stats:
    def __init__(self) -> None:
        self.bet_sum = 0
        self.ret_sum = 0
        self.bet_count = 0
        self.hit_count = 0

    def add(self, bet: int, ret: int) -> None:
        self.bet_sum += bet
        self.ret_sum += ret
        self.bet_count += 1
        if ret > 0:
            self.hit_count += 1

    def roi_pct(self) -> float:
        if self.bet_sum <= 0:
            return float("nan")
        return (self.ret_sum / self.bet_sum - 1) * 100

    def hit_rate_pct(self) -> float:
        if self.bet_count <= 0:
            return float("nan")
        return (self.hit_count / self.bet_count) * 100

    def profit(self) -> int:
        return self.ret_sum - self.bet_sum


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


def main() -> None:
    strat = Strategy(
        name="R (G1/G3 score, exp 1.30-1.45, prob>=0.04, rank<=7, wind<=3)",
        grade_whitelist={"G1", "G3"},
        sort_key="score",
        min_exp=1.30,
        max_exp=1.45,
        min_prob=0.04,
        max_rank=7,
        max_wind=3.0,
    )

    overall = Stats()
    by_year: dict[str, Stats] = defaultdict(Stats)
    by_month: dict[str, Stats] = defaultdict(Stats)
    by_grade: dict[str, Stats] = defaultdict(Stats)
    by_venue: dict[str, Stats] = defaultdict(Stats)
    by_wind_bucket: dict[str, Stats] = defaultdict(Stats)

    with open(FILE_PATH, "r", encoding="utf-8", newline="") as f:
        reader = csv.reader(f, delimiter="\t")
        header = next(reader)
        col = {name: i for i, name in enumerate(header)}

        idx_ymd = col["ymd"]
        idx_jyocd = col["jyocd"]
        idx_raceno = col["raceno"]
        idx_grade = col["grade"]
        idx_wind = col["wind"]
        idx_exp = col["expect_bor"]
        idx_prob = col["probability"]
        idx_rank = col["bet_oddsrank"]
        idx_betamt = col["betamt"]
        idx_hitamt = col["hitamt"]

        current_key = None
        current_ymd = ""
        current_grade = ""
        current_jyocd = ""
        current_wind = 0.0
        keep_race = False
        bets: list[tuple[float, float, int, int, int]] = []  # exp, prob, rank, betamt, hitamt

        def flush() -> None:
            nonlocal bets
            if current_key is None:
                return
            if not keep_race or not bets:
                bets = []
                return

            if strat.sort_key == "score":
                bets_sorted = sorted(bets, key=lambda x: x[0] * x[1], reverse=True)
            else:
                bets_sorted = sorted(bets, key=lambda x: x[0], reverse=True)

            selected = None
            for exp, prob, rank, betamt, hitamt in bets_sorted:
                if exp < strat.min_exp:
                    break
                if exp > strat.max_exp:
                    continue
                if prob < strat.min_prob:
                    continue
                if rank > strat.max_rank:
                    continue
                selected = (betamt, hitamt)
                break

            if selected is None:
                bets = []
                return

            betamt, hitamt = selected
            overall.add(betamt, hitamt)
            yy = current_ymd[:4]
            ym = current_ymd[:6]
            by_year[yy].add(betamt, hitamt)
            by_month[ym].add(betamt, hitamt)
            by_grade[current_grade].add(betamt, hitamt)
            by_venue[current_jyocd].add(betamt, hitamt)

            w = current_wind
            if w <= 1:
                wb = "0-1"
            elif w <= 2:
                wb = "1-2"
            elif w <= 3:
                wb = "2-3"
            else:
                wb = ">3"
            by_wind_bucket[wb].add(betamt, hitamt)

            bets = []

        for row in reader:
            ymd = row[idx_ymd]
            key = (ymd, row[idx_jyocd], row[idx_raceno])

            if current_key is None:
                current_key = key
                current_ymd = ymd
                current_grade = row[idx_grade]
                current_jyocd = row[idx_jyocd]
                current_wind = _to_float(row[idx_wind])
                keep_race = current_grade in strat.grade_whitelist and current_wind <= strat.max_wind
            elif key != current_key:
                flush()
                current_key = key
                current_ymd = ymd
                current_grade = row[idx_grade]
                current_jyocd = row[idx_jyocd]
                current_wind = _to_float(row[idx_wind])
                keep_race = current_grade in strat.grade_whitelist and current_wind <= strat.max_wind

            if not keep_race:
                continue

            exp = _to_float(row[idx_exp])
            prob = _to_float(row[idx_prob])
            rank = _to_int(row[idx_rank])
            betamt = _to_int(row[idx_betamt])
            hitamt = _to_int(row[idx_hitamt])
            bets.append((exp, prob, rank, betamt, hitamt))

        flush()

    print("=" * 80)
    print("Strategy segment analysis")
    print("=" * 80)
    print(strat.name)
    print(f"overall: bets={overall.bet_count} profit={overall.profit():,} ROI%={overall.roi_pct():.2f} hit%={overall.hit_rate_pct():.2f}")

    print("\n=== by year ===")
    for y in sorted(by_year.keys()):
        st = by_year[y]
        print(f"{y}: bets={st.bet_count} profit={st.profit():,} ROI%={st.roi_pct():.2f} hit%={st.hit_rate_pct():.2f}")

    print("\n=== by grade ===")
    for g in sorted(by_grade.keys()):
        st = by_grade[g]
        print(f"{g}: bets={st.bet_count} profit={st.profit():,} ROI%={st.roi_pct():.2f} hit%={st.hit_rate_pct():.2f}")

    print("\n=== by wind bucket ===")
    for wb in ["0-1", "1-2", "2-3", ">3"]:
        if wb not in by_wind_bucket:
            continue
        st = by_wind_bucket[wb]
        print(f"wind {wb}: bets={st.bet_count} profit={st.profit():,} ROI%={st.roi_pct():.2f} hit%={st.hit_rate_pct():.2f}")

    print("\n=== by venue (top 15 by profit, min 10 bets) ===")
    venue_rows = []
    for v, st in by_venue.items():
        if st.bet_count < 10:
            continue
        venue_rows.append((st.profit(), st.roi_pct(), st.bet_count, v))
    venue_rows.sort(reverse=True)
    for prof, roi, cnt, v in venue_rows[:15]:
        print(f"jyocd={v}: bets={cnt} profit={prof:,} ROI%={roi:.2f}")

    print("\n=== by venue (worst 10 by profit, min 10 bets) ===")
    for prof, roi, cnt, v in venue_rows[-10:]:
        print(f"jyocd={v}: bets={cnt} profit={prof:,} ROI%={roi:.2f}")

    print("\n=== by month (chronological) ===")
    months = sorted(by_month.keys())
    for ym in months:
        st = by_month[ym]
        print(f"{ym}: bets={st.bet_count} profit={st.profit():,} ROI%={st.roi_pct():.2f}")


if __name__ == "__main__":
    main()
