import json
import os


def output(file_name: str) -> str:
    return f"./scripts/data/{file_name}"


def read_raw_data_string(file_name: str) -> str:
    with open(f"./scripts/raw_data/{file_name}", "r") as fp:
        strs = fp.readlines()
    return "\n".join(strs)


def read_raw_data_json(file_name: str) -> dict:
    with open(f"./scripts/raw_data/{file_name}", "r") as fp:
        return json.load(fp)


def ensure_path(file_name: str) -> str:
    dirpath = os.path.dirname(file_name)
    if dirpath and not os.path.exists(dirpath):
        os.makedirs(dirpath, exist_ok=True)
    return file_name
