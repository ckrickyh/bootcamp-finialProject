import importlib.util
from pathlib import Path
import unittest
from unittest.mock import Mock, patch

import pandas as pd

from pythonProjects import FYPHistory2DB


class FYPHistory2DBTests(unittest.TestCase):
    def test_import_does_not_fetch_data_or_connect_to_database(self):
        script = Path(FYPHistory2DB.__file__)
        spec = importlib.util.spec_from_file_location("fyp_history2db_import_check", script)
        module = importlib.util.module_from_spec(spec)
        with patch("requests.get") as get, patch("sqlalchemy.create_engine") as create_engine:
            spec.loader.exec_module(module)
        get.assert_not_called()
        create_engine.assert_not_called()

    def test_fetch_history_uses_mocked_yahoo_response_and_fills_missing_values(self):
        response = Mock()
        response.json.return_value = {
            "chart": {
                "result": [
                    {
                        "timestamp": [1000, 2000],
                        "indicators": {
                            "quote": [
                                {
                                    "open": [10.0, None],
                                    "high": [11.0, 12.0],
                                    "low": [9.0, 10.0],
                                    "close": [10.5, 11.5],
                                    "volume": [100, 200],
                                }
                            ]
                        },
                    }
                ]
            }
        }
        get = Mock(return_value=response)

        history = FYPHistory2DB.fetch_history(["ABC"], end=3000, get=get)

        response.raise_for_status.assert_called_once_with()
        get.assert_called_once()
        self.assertIn("period2=3000", get.call_args.args[0])
        self.assertEqual(history["code"].tolist(), ["ABC", "ABC"])
        self.assertEqual(history["open"].tolist(), [10.0, 10.0])

    def test_missing_database_url_fails_without_connecting(self):
        with patch.dict("os.environ", {}, clear=True):
            with self.assertRaisesRegex(RuntimeError, "DATABASE_URL"):
                FYPHistory2DB.get_database_url()

    def test_database_writes_are_replaceable_and_dispose_engine(self):
        engine = Mock()
        history = pd.DataFrame({"code": ["ABC"]})
        logos = pd.DataFrame({"symbol": ["ABC"]})
        with patch.object(FYPHistory2DB, "create_engine", return_value=engine) as create_engine:
            with patch.object(pd.DataFrame, "to_sql") as to_sql:
                FYPHistory2DB.write_to_database(history, logos, "postgresql://test")

        create_engine.assert_called_once_with("postgresql://test")
        self.assertEqual(
            [write.args[0] for write in to_sql.call_args_list],
            ["ohlc_data", "stock_logo"],
        )
        engine.dispose.assert_called_once_with()


if __name__ == "__main__":
    unittest.main()
