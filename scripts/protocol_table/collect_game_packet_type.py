import pandas as pd
import re
from ..utils.file import read_raw_data_json, output, ensure_path
import functools as f


def process_packet_registry_code(code: str) -> pd.DataFrame:
    # remove all blanks
    code = code.replace(" ", "").replace("\n", "")
    regex = re.compile(
        r"PacketType<.*?>\s*([A-Z0-9_]+)\s*=\s*(createClientbound|createServerbound)\(\"([a-z0-9_]+)\"\);")
    results = []
    for match in regex.finditer(code):
        type_name = match.group(1)
        type_id = match.group(3)
        method = match.group(2)
        packet_flow = "S2C" if "Clientbound" in method else "C2S"
        results.append({
            "packet type": type_name,
            "packet id": type_id,
            "flow": packet_flow
        })
    return pd.DataFrame(results)


def process_protocol() -> None:
    raw = read_raw_data_json("game_type_protocols.json")
    versioned_df = {}
    for key, value in raw.items():
        df = process_packet_registry_code(value)
        # new_columns = {col: f"{col} : {key}" if col == "packet type" else col for col in df.columns}
        df[key] = "√"
        # df = df.rename(columns=new_columns)
        versioned_df[key] = df
    df_list = list(versioned_df.values())
    merged = f.reduce(lambda x, y: pd.merge(x, y, on=["packet id", "flow", "packet type"], how="outer"), df_list)
    merged.fillna("❌", inplace=True)
    merged.to_csv(ensure_path(output("protocols/table.csv")))
    merged.to_excel(ensure_path(output("protocols/table.xlsx")))

