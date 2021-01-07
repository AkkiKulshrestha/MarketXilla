package in.techxilla.www.marketxilla.webservices;

public class RestClient
	{
		private static API REST_CLIENT;

		public static String ROOT_URL = "https://marketxilla.com/marketxilla_app/";
		public static String NSE_URL = "https://www1.nseindia.com/live_market/dynaContent/";

		public static String NSE_GET_QUOTE_URL = NSE_URL+"live_watch/get_quote/GetQuote.jsp?symbol=";

		public static String NSE_MARKET_STATUS_URL = "https://www1.nseindia.com//emerge/homepage/smeNormalMktStatus.json";
		public static String NSE_INDEX_WATCH_URL = NSE_URL+"live_watch/stock_watch/liveIndexWatchData.json";
		public static String NSE_ADVANCE_DECLINE_URL = "https://www1.nseindia.com/common/json/indicesAdvanceDeclines.json";

		public static String NSE_GET_FUTURE_STOCK_URL= "https://www.nseindia.com/api/snapshot-derivatives-equity?index=futures";
		public static String GAINER_URL = NSE_URL+"live_analysis/gainers/niftyGainers1.json";
		public static String LOSER_URL =  NSE_URL+"live_analysis/losers/niftyLosers1.json";

		public static String NSE_GET_CallsOPTSTK_URL = NSE_URL+"live_analysis/most_active/CallsOPTSTKVolume.json";
		public static String NSE_GET_PutsOPTSTK_URL = NSE_URL+"live_analysis/most_active/PutsOPTSTKVolume.json";

		public static String NSE_GET_CallsALLVolume_URL = NSE_URL+"live_analysis/most_active/CallsALLVolume.json";
		public static String NSE_GET_PutsALLVolume_URL = NSE_URL+"live_analysis/most_active/PutsALLVolume.json";

		public static String TOP_VALUE_URL = NSE_URL+"live_analysis/most_active/allTopValue1.json";
		public static String TOP_VOLUME_URL = NSE_URL+"live_analysis/most_active/allTopVolume1.json";

		public static String URLProd = "http://www.mymfnow.com/api";



		public static String URLDev = "http://13.126.98.215:8081";

		public static String GenerateQrCode = "https://pierre2106j-qrcode.p.rapidapi.com/api?backcolor=ffffff&pixel=10&ecl=L+%7C+M%7C+Q+%7C+H&forecolor=000000&type=json+%7C+url+%7C+tel+%7C+sms+%7C+email&";



		public static String Development = "http://103.87.174.12/fubiqmfapp/api/User/";
		/*static
			{
				setupRestClient();
			}*/


		private RestClient()
			{
			}

		public static API get()
			{
				return REST_CLIENT;
			}

		/*private static void setupRestClient()
			{
				RestAdapter.Builder builder = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(ROOT).setClient(new OkClient(new OkHttpClient()));
				RestAdapter restAdapter = builder.build();
				REST_CLIENT = restAdapter.create(API.class);
			}*/
	}
