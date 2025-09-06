# %% [markdown]
# # Read yahoo history

# %%
import requests
from pandas import json_normalize
import pandas as pd
import datetime

# %% [markdown]
# ## setting

# %%
now = datetime.datetime.now()
res = int(now.timestamp() * 1000) #end unix date

stockLst = [
        "TSLA", "NVDA", "AAPL", "META", "GOOGL",
        "MSFT", "INTC", "AVGO", "SOUN", "AMD",
        "NFLX", "UBER", "TGT", "BBAI", "OSS"
        ,"DNUT", "COMP", "OPEN", "MARA", "IOVA"
        ,"WMT",  "HD", "AMZN", "NIO", "COST"
        ,"PYPL", "ADBE", "CRM", "ORCL", "IBM"
        ,"CAH", "ACGL", "BLL", "CEGVV", "CLX"
        ,"CL", "LMT", "CPRT", "CAH", "MDLZ"
        ,"FANG",  "EL", "EXC", "LOW", "MAR"
        ,"GILD", "HAL", "LMT", "MCD", "NKE"
        ,"LHX", "MET", "MO", "MS", "V"
        ,"LLY", "ORCL", "JNJ", "PG", "KO"
      ]

start_unix_date = "1657237004"
end_unix_date = res

headers = {
    "User-Agent": "Mozilla/5.0"
}

# tslaUrl = r'https://query1.finance.yahoo.com/v8/finance/chart/NVDA?period1=1657237004&period2=1751931404&interval=1d&events=history'
# tslaUrl = '/content/sample_data/TSLA.json'

# %%
def stockUrl(stock):
  return f'https://query1.finance.yahoo.com/v8/finance/chart/{stock}?period1={start_unix_date}&period2={end_unix_date}&interval=1d&events=history'

def dfTreatment(url, headers):
  response = requests.get(url, headers = headers)
  data = response.json()
  if (data['chart']['result'] is not None):
    df_metaData = json_normalize(data['chart']['result'])
    df_indicator = pd.DataFrame(df_metaData['indicators.quote'][0])
    return df_metaData, df_indicator
  else:
    return None, None

def dfExtract(df_metaData, df_indicator, dfStock, dfAll, i):
  if df_metaData is not None:
    for j in df_indicator.columns.to_list():
      dfStock[j] = df_indicator[j][0]
    dfStock['code'] = i
    dfStock['datetime'] = df_metaData['timestamp'][0]
    #print(dfStock.columns.to_list())
  return dfStock

def dfConact(dfAll, dfStock):
  if dfAll.size == 0:
    dfAll=pd.DataFrame(columns=dfStock.columns.to_list())

  dfAll = pd.concat([dfAll, dfStock], ignore_index = True)
  return dfAll

def dfFillNA(dfAll):
  if dfAll.isna().any().any() == "np.True_":
    dfAll = dfAll.fillna(method = "ffill")
    msg = 'NA value'
  else:
    msg = 'no NA'
  # dfAll.to_csv("OLHC.csv")
  return dfAll, msg


# %%
dfAll = pd.DataFrame()

for i in stockLst:

  url = stockUrl(i)

  df_3, df_4 = dfTreatment(url, headers)
  
  dfStock = pd.DataFrame()

  dfStock = dfExtract(df_3, df_4, dfStock, dfAll,i)

  dfAll = dfConact(dfAll, dfStock)

dfAll['datetime'] = pd.to_datetime(dfAll['datetime'], unit="s").dt.date
dfAll, msg = dfFillNA(dfAll)
dfAll = dfAll.sort_values(by=['code', 'datetime'])

print(f"Completed! {msg}" )

# %%
dfAll

# %% [markdown]
# ## df_logo

# %%
df_logo = pd.read_csv("Stock_list.csv")
df_logo["symbol_link"] = "https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/"+ df_logo["symbol"]+".png"
# df_logo["symbol_link"] = "https://eodhd.com/img/logos/US/"+ df_logo["symbol"]+".png"

# %%
dfAll = dfAll.merge(df_logo, left_on='code', right_on='symbol')
dfAll.drop(['symbol'], axis = 1, inplace = True)
dfAll['index'] = dfAll.index
dfAll

# %% [markdown]
# # pandas write to DB

# %%
import pandas as pd
from sqlalchemy import create_engine
from sqlalchemy import Table, Column, MetaData, Integer, String, Date, Float, BigInteger

# postgres -> user name
# admin1234 -> password
# bootcamp_2504 -> database name

# localhost
# bootcamp_engine = create_engine("postgresql+psycopg2://postgres:admin1234@localhost:5432/bootcamp_2504")
# Docker
# bbootcamp_engine = create_engine("postgresql+psycopg2://postgres:admin1234@localhost:5532/bootcamp_2504")

engineLst = ["postgresql+psycopg2://postgres:admin1234@localhost:5432/bootcamp_2504",
             "postgresql+psycopg2://postgres:admin1234@localhost:5532/bootcamp_2504"] #5432, 5532

for engine in engineLst:
  bootcamp_engine = create_engine(engine)

  # Define table structure
  metadata = MetaData()

  ohlc_data_schema = Table(
    "ohlc_data", metadata,  #Table name
    Column("id", BigInteger, primary_key=True),
    Column("code", String(10), nullable=False),
    Column("date", Date, nullable=False),
    Column("open", Float, nullable=False),
    Column("high", Float, nullable=False),
    Column("low", Float, nullable=False),
    Column("close", Float, nullable=False),
    Column("volume", BigInteger, nullable=False),
    Column("symbolink", String, nullable=False)
  )

  metadata.create_all(bootcamp_engine)

  # table name = ohlc_data
  # auto create table
  # "replace" -> delete and insert
  # "append" -> insert
  dfAll.to_sql("ohlc_data", bootcamp_engine, index=False, if_exists="replace")
  print(f'OK: {engine}')

  # ==========================================================
  # Define logo table structure
  metadata = MetaData()

  stock_logo_schema = Table(
    "stock_logo", metadata,  #Table name
    Column("symbol", String(15), primary_key=True),
    Column("logo_link", String(100), nullable=True)
  )
  metadata.create_all(bootcamp_engine)
  df_logo.to_sql("stock_logo", bootcamp_engine, index=False, if_exists="replace")


# %%
df_logo


