import datetime
import os
from pathlib import Path

import pandas as pd
import requests
from sqlalchemy import create_engine


CURRENT_DIR = Path(__file__).resolve().parent
START_UNIX_DATE = "1657237004"
STOCK_LIST = [
    "TSLA", "NVDA", "AAPL", "META", "GOOGL",
    "MSFT", "INTC", "AVGO", "SOUN", "AMD",
    "NFLX", "UBER", "TGT", "BBAI", "OSS",
    "DNUT", "COMP", "OPEN", "MARA", "IOVA",
    "WMT", "HD", "AMZN", "NIO", "COST",
    "PYPL", "ADBE", "CRM", "ORCL", "IBM",
    "CAH", "ACGL", "BLL", "CEGVV", "CLX",
    "CL", "TECK", "CPRT", "MDLZ",
    "FANG", "EL", "EXC", "LOW", "MAR",
    "GILD", "HAL", "LMT", "MCD", "NKE",
    "LHX", "MET", "MO", "MS", "V",
    "LLY", "JNJ", "PG", "KO",
    "TSM", "PDD", "CRWV", "PLTR", "HOOD",
    "SMCI", "IREN", "WFC", "XOM", "APH",
]
HEADERS = {"User-Agent": "Mozilla/5.0"}


def stock_url(stock, start=START_UNIX_DATE, end=None):
    if end is None:
        end = int(datetime.datetime.now().timestamp())
    return (
        f"https://query1.finance.yahoo.com/v8/finance/chart/{stock}"
        f"?period1={start}&period2={end}&interval=1d&events=history"
    )


def fetch_stock_history(stock, start=START_UNIX_DATE, end=None, get=None):
    if get is None:
        get = requests.get
    response = get(stock_url(stock, start, end), headers=HEADERS, timeout=30)
    response.raise_for_status()
    result = response.json().get("chart", {}).get("result")
    if not result:
        return pd.DataFrame()

    stock_data = result[0]
    quote = stock_data.get("indicators", {}).get("quote", [{}])[0]
    frame = pd.DataFrame(quote)
    frame["code"] = stock
    frame["datetime"] = stock_data.get("timestamp", [])
    return frame


def fetch_history(stock_symbols=STOCK_LIST, start=START_UNIX_DATE, end=None, get=None):
    frames = [
        fetch_stock_history(stock, start=start, end=end, get=get)
        for stock in stock_symbols
    ]
    frames = [frame for frame in frames if not frame.empty]
    if not frames:
        raise ValueError("Yahoo Finance returned no stock history.")

    history = pd.concat(frames, ignore_index=True)
    history["datetime"] = pd.to_datetime(history["datetime"], unit="s").dt.date
    history = history.sort_values(["code", "datetime"]).reset_index(drop=True)
    value_columns = [column for column in history.columns if column not in {"code", "datetime"}]
    history[value_columns] = history.groupby("code", sort=False)[value_columns].ffill()
    return history


def load_stock_logos(path=CURRENT_DIR / "Stock_List.csv"):
    logos = pd.read_csv(path)
    logos["symbol_link"] = (
        "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/"
        + logos["symbol"]
        + ".png"
    )
    return logos


def prepare_database_frames(history, logos):
    history = history.merge(logos, left_on="code", right_on="symbol")
    history = history.drop(columns=["symbol"])
    history["index"] = history.index
    return history, logos


def get_database_url():
    database_url = os.getenv("DATABASE_URL")
    if not database_url:
        raise RuntimeError("Set the DATABASE_URL environment variable.")
    return database_url


def write_to_database(history, logos, database_url):
    engine = create_engine(database_url)
    try:
        history.to_sql("ohlc_data", engine, index=False, if_exists="replace")
        logos.to_sql("stock_logo", engine, index=False, if_exists="replace")
    finally:
        engine.dispose()


def main():
    from dotenv import load_dotenv

    load_dotenv(dotenv_path=CURRENT_DIR.parent / ".env")
    database_url = get_database_url()
    history = fetch_history()
    logos = load_stock_logos()
    history, logos = prepare_database_frames(history, logos)
    write_to_database(history, logos, database_url)
    print(f"Completed! {len(history)} history rows written.")


if __name__ == "__main__":
    main()
