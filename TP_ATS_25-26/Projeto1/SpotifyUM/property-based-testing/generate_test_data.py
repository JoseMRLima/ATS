import csv
import random
import warnings
from pathlib import Path
from hypothesis import strategies as st

NUM_CASES = 200

ROOT = Path(__file__).resolve().parent.parent
OUTPUT_DIR = ROOT / "src" / "test" / "resources" / "property-data"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

plan_strategy = st.sampled_from(["Free", "PremiumBase", "PremiumTop"])
points_strategy = st.integers(min_value=0, max_value=50_000)
plays_strategy = st.integers(min_value=0, max_value=30)
duration_strategy = st.integers(min_value=30, max_value=600)
num_musics_strategy = st.integers(min_value=0, max_value=25)
genre_strategy = st.sampled_from(["Rock", "Pop", "Jazz", "Classical", "Electronic", "HipHop"])

# identifiers sem vírgulas/aspas para não complicar o CSV
name_strategy = st.text(
    alphabet=st.characters(whitelist_categories=("Lu", "Ll", "Nd")),
    min_size=3,
    max_size=15,
)


def expected_plan_points(plan_type: str, initial_points: int, num_calls: int) -> int:
    # espelha a lógica de Plan.addPoints() em Java
    points = initial_points
    for _ in range(num_calls):
        if plan_type == "Free":
            points += 5
        elif plan_type == "PremiumBase":
            points += 10
        elif plan_type == "PremiumTop":
            points = int(points + 0.025 * points)
    return points


def draw(strategy):
    with warnings.catch_warnings():
        warnings.simplefilter("ignore")
        return strategy.example()


def gen_plan_points():
    path = OUTPUT_DIR / "plan_points.csv"
    with open(path, "w", newline="") as f:
        w = csv.writer(f)
        w.writerow(["plan", "initial_points", "num_calls", "expected_points"])
        for _ in range(NUM_CASES):
            plan = draw(plan_strategy)
            initial = draw(points_strategy)
            calls = draw(plays_strategy)
            w.writerow([plan, initial, calls, expected_plan_points(plan, initial, calls)])
    print(f"[OK] {NUM_CASES} casos -> {path.relative_to(ROOT)}")


def gen_music_reproductions():
    path = OUTPUT_DIR / "music_reproductions.csv"
    with open(path, "w", newline="") as f:
        w = csv.writer(f)
        w.writerow(["music_name", "duration", "num_plays", "expected_reproductions"])
        for _ in range(NUM_CASES):
            name = draw(name_strategy)
            duration = draw(duration_strategy)
            plays = draw(plays_strategy)
            w.writerow([name, duration, plays, plays])
    print(f"[OK] {NUM_CASES} casos -> {path.relative_to(ROOT)}")


def gen_album_size():
    path = OUTPUT_DIR / "album_size.csv"
    with open(path, "w", newline="") as f:
        w = csv.writer(f)
        w.writerow(["album_name", "artist", "num_musics", "expected_size"])
        for _ in range(NUM_CASES):
            album = draw(name_strategy)
            artist = draw(name_strategy)
            n = draw(num_musics_strategy)
            w.writerow([album, artist, n, n])
    print(f"[OK] {NUM_CASES} casos -> {path.relative_to(ROOT)}")


def gen_plan_permissions():
    path = OUTPUT_DIR / "plan_permissions.csv"
    permissions = {
        "Free":        (False, False, False, False),
        "PremiumBase": (True,  True,  True,  False),
        "PremiumTop":  (True,  True,  True,  True),
    }
    with open(path, "w", newline="") as f:
        w = csv.writer(f)
        w.writerow(["plan", "can_access_library", "can_skip",
                    "can_choose_what_to_play", "has_access_to_favorites"])
        for _ in range(NUM_CASES):
            plan = draw(plan_strategy)
            lib, skip, choose, fav = permissions[plan]
            w.writerow([plan, lib, skip, choose, fav])
    print(f"[OK] {NUM_CASES} casos -> {path.relative_to(ROOT)}")


if __name__ == "__main__":
    random.seed(42)
    gen_plan_points()
    gen_music_reproductions()
    gen_album_size()
    gen_plan_permissions()
    print(f"\nCSVs escritos em: {OUTPUT_DIR.relative_to(ROOT)}")
