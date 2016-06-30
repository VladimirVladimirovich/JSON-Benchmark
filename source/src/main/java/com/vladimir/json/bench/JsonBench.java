package com.vladimir.json.bench;

import com.vladimir.json.data.*;
import com.vladimir.json.libs.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Vector;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author Vladimir
 */
public class JsonBench {
        private ArrayList<ResultData> resultDataList;
        private ArrayList<String> usedTestNames;
        private ArrayList<String> usedLibsNames;
        ArrayList<Vector<String>> rowVectorList;
        ArrayList<Vector<String>> rowVectorListPercent;
    
	private int threadPause = 40;
	private int loopCount;
        private int testsCount;
        
        private TestType testType;
        private String fileString;

	@SuppressWarnings("rawtypes")
	Class[] clazz = new Class[] { Moshi.class, LoganSquare.class, Genson.class, GoogleGson.class, JsonJava.class,
             JsonFast.class, JsonSimple.class, JsonIJ.class, Jackson.class, JsonSmart.class };
        
         String[] testTypes = new String [] {"text", "int", "float", "bool", "unicode", "mix"};
        
        public JsonBench(int iterationsCount, int testsCount) {
            resultDataList = new ArrayList<ResultData>();
            usedTestNames = new ArrayList<String>();
            usedLibsNames = new ArrayList<String>();
            rowVectorList = new ArrayList<Vector<String>>();
            rowVectorListPercent = new ArrayList<Vector<String>>();
            
            this.loopCount = iterationsCount;
            this.testsCount = testsCount;
        }
        
        public JsonBench(int iterationsCount, int testsCount, String fileString) {
            resultDataList = new ArrayList<ResultData>();
            usedTestNames = new ArrayList<String>();
            usedLibsNames = new ArrayList<String>();
            rowVectorList = new ArrayList<Vector<String>>();
            rowVectorListPercent = new ArrayList<Vector<String>>();
            
            this.loopCount = iterationsCount;
            this.testsCount = testsCount;
            this.fileString = fileString;
        }
        
        public long doBenchmark(Integer testIndex, Integer libIndex, TestType testType) {
            this.testType = testType;
            
            try {
                return start(testIndex, libIndex);
            } catch (Exception e) {}
            
            return -1;
        }
        
        public void processResultDataList() {
            Vector<String> rowVector = new Vector<String>();
            Vector<String> rowVectorPercent = new Vector<String>();
            ArrayList<Long> bestAvarages = new ArrayList<Long>();
            
            long avarage = 0;
            long best = Long.MAX_VALUE;
            
            for(int test = 0; test < usedTestNames.size(); test++) {
                rowVector.add(usedTestNames.get(test));
                
                for(int lib = 0; lib < usedLibsNames.size(); lib++) {
                    for(ResultData data : resultDataList) {
                        if(data.getTestName().equals(usedTestNames.get(test)) && data.getLibName().equals(usedLibsNames.get(lib)))
                            avarage += data.getResultTime();
                    }
                    
                    avarage = avarage / testsCount;
                    rowVector.add(String.valueOf(avarage) + " ms");
                    
                    if(avarage < best)
                        best = avarage;
                    
                    avarage = 0;
                }
                
                bestAvarages.add(best);
                rowVectorList.add((Vector<String>)rowVector.clone());
                rowVector.clear();
                best = Long.MAX_VALUE;
            }
            
            avarage = 0;
            
            for(int test = 0; test < usedTestNames.size(); test++) {
                rowVectorPercent.add(usedTestNames.get(test));
                
                for(int lib = 0; lib < usedLibsNames.size(); lib++) {
                    for(ResultData data : resultDataList) {
                        if(data.getTestName().equals(usedTestNames.get(test)) && data.getLibName().equals(usedLibsNames.get(lib)))
                            avarage += data.getResultTime();
                    }
                    
                    avarage = avarage / testsCount;
                    rowVectorPercent.add(String.valueOf(avarage * 100 / bestAvarages.get(test)) + " %");

                    avarage = 0;
                }
                
                rowVectorListPercent.add((Vector<String>)rowVectorPercent.clone());
                rowVectorPercent.clear();
            }
        }
        
        public ArrayList<Vector<String>> getRowVectorList() {
            return rowVectorList;
        }
        
        public ArrayList<Vector<String>> getRowVectorListPercent() {
            return rowVectorListPercent;
        }
        
        public ArrayList<String> getUsedLibsList() {
            return usedLibsNames;
        }

	// GoogleGson.class,
	//public  void main(String[] args) throws Exception {
		 //start(new String[] { "test" });
		// start(new String[] { "showResult" });
		// start(new String[] { "showScript" });
		 //start(new String[] { "bench", "mixte", "9"});
		//start(args);
	//}

	private String getDeep(int deep) {
		StringBuilder sb = new StringBuilder(deep * 2);
		for (int i = 0; i < deep; i++)
			sb.append('[');
		for (int i = 0; i < deep; i++)
			sb.append(']');
		return sb.toString();
	}

	private String getDeep2(int deep) {
		StringBuilder sb = new StringBuilder(deep * 6 + 10);
		for (int i = 0; i < deep; i++)
			sb.append("{\"a\":");		
		sb.append("\"Done\"");
		for (int i = 0; i < deep; i++)
			sb.append('}');
		return sb.toString();
	}

	private void testCompression() throws Exception {
		ArrayList<String> list = BenchData.getTestMessages("mixte", false);
		test1(list);
		long T = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++)
			test1(list);
		T = System.currentTimeMillis() - T;
		System.out.println(T);
		// 657
	}

	private void test1(ArrayList<String> list) throws Exception {
		for (String s : list) {
			// 530
			// JSONValue.compress(s, JSONStyle.MAX_COMPRESS);
			// 640
			JSONValue.compress(s);
			// System.out.println(s);
			// System.out.println(s2);
			// System.out.println();
		}
	}

	private long start(int testIndex, int libIndex) throws Exception {
                    int impId = libIndex;
                    String testName = "Custom";
                    
                    if(fileString == null) {
                        testName = testTypes[testIndex];
                        
                        if(!usedTestNames.contains(testName))
                            usedTestNames.add(testName);
                        
                        //BenchData.cleanCache();
                        //BenchData.changeTest(testName);
                    }
                    else {
                        if(!usedTestNames.contains(testName))
                            usedTestNames.add(testName);
                    }
                    
                    if (impId >= clazz.length)
			return -1;
		
                    String apiName = ((JsonInter) (clazz[impId].newInstance())).getSimpleName();
                    
                    if(!usedLibsNames.contains(apiName))
                        usedLibsNames.add(apiName);

                    long ms = -1;
                    
                    try {
                        ms = bench(impId);
                        resultDataList.add(new ResultData(testName, apiName, ms));
                        return ms;
			//addResult(cmd, testName, apiName, ms);
                    } catch (Exception e) { }
                    
		/*} else if (cmd.startsWith("showResult")) {
                    formatResult();
		} else if (cmd.startsWith("showScript")) {
                        showScript();
		}*/
                    return -1;
	}
        
        @SuppressWarnings("rawtypes")
	private void bench() throws Exception {
            ArrayList<String> msgs = BenchData.getTestMessages();
            
            for (Class c : clazz) {
                bench(c, msgs);
            }
	}

	private long bench(int classId) throws Exception {
            if (classId >= clazz.length)
                return -1;
            
            long res = 0;
            
            if(fileString == null)
                res = bench(clazz[classId], BenchData.getTestMessages());
            else
                res = bench(clazz[classId]);
            
            return res;
	}
        
        private long bench(Class c) throws Exception {
            JsonInter p = (JsonInter) c.newInstance();
            
            long time = 0;

            switch(testType) {
                case PARSE:
                    time = doParsingBenchmark(p);
                    break;
                case GENERATE:
                    time = doGeneratingBenchmark(p);
                    break;
            }

            return time;
        }
        
        private long doParsingBenchmark(JsonInter p) throws Exception {
            try {
                if(fileString.startsWith("[")) {
                    if(p.getSimpleName().equals("GSON") || p.getSimpleName().equals("JSON java")) {
                        long T1 = System.currentTimeMillis();
                
                        for (int i = 0; i < loopCount; i++) {
                            p.parseArray(fileString);
                        }

                        T1 = System.currentTimeMillis() - T1;

                        System.out.println("Custom json> " + p.getSimpleName() + " : " + T1 + "ms");
                        return T1;
                    }
                }

                long T1 = System.currentTimeMillis();
                
                for (int i = 0; i < loopCount; i++) {
                    p.parseObj(fileString);
		}
		
                T1 = System.currentTimeMillis() - T1;
                
                System.out.println("Custom json> " + p.getSimpleName() + " : " + T1 + "ms");
		return T1;
            } catch (Exception e) {
                // System.out.println("Parser error " + e);
		// System.out.println("failed on " + text);
		// e.printStackTrace();
            }
            
            return -1;
        }
        
        private long doGeneratingBenchmark(JsonInter p) throws Exception {
            if(fileString.startsWith("[")) {
                    if(p.getSimpleName().equals("GSON") || p.getSimpleName().equals("JSON java")) {
                        Object obj = p.parseArray(fileString);
                        
                        if(p.getSimpleName().equals("GSON")) {
                            long T1 = System.currentTimeMillis();
                
                            for (int i = 0; i < loopCount; i++) {
                                p.toJsonStringArray(obj);
                            }

                            T1 = System.currentTimeMillis() - T1;

                            System.out.println("Custom json> " + p.getSimpleName() + " : " + T1 + "ms");
                            return T1;
                        }
                        
                        long T1 = System.currentTimeMillis();
                
                        for (int i = 0; i < loopCount; i++) {
                            p.toJsonString(obj);
                        }

                        T1 = System.currentTimeMillis() - T1;

                        System.out.println("Custom json> " + p.getSimpleName() + " : " + T1 + "ms");
                        return T1;
                    }
            }
    
            Object obj = p.parseObj(fileString);
            
            try {
                long T1 = System.currentTimeMillis();
                
                for (int i = 0; i < loopCount; i++) {
                    p.toJsonString(obj);
		}
		
                T1 = System.currentTimeMillis() - T1;
                
                System.out.println("Custom json> " + p.getSimpleName() + " : " + T1 + "ms");
                
		return T1;
            } catch (Exception e) {
                // System.out.println("Parser error " + e);
		// System.out.println("failed on " + text);
		// e.printStackTrace();
            }
            
            return -1;
        }

	@SuppressWarnings("rawtypes")
	private long bench(Class c, ArrayList<String> msgs) throws Exception {
            int nbText = msgs.size();
            JsonInter p = (JsonInter) c.newInstance();
            
            long time = 0;

            switch(testType) {
                case PARSE:
                    time = doParsingBenchmark(msgs, p, nbText);
                    break;
                case GENERATE:
                    time = doGeneratingBenchmark(msgs, p, nbText);
                    break;
            }

            return time;
        }
        
        private long doParsingBenchmark(ArrayList<String> msgs, JsonInter p, int nbText) throws Exception {
            String text = null;
            
            try {
                long T1 = System.currentTimeMillis();
                
                for (int i = 0; i < loopCount; i++) {
                    text = msgs.get(i % nbText);
                    p.parseObj(text);
		}
		
                T1 = System.currentTimeMillis() - T1;
                
                System.out.println(BenchData.testMode + "> " + p.getSimpleName() + " : " + T1 + "ms");
                
		return T1;
            } catch (Exception e) {
                // System.out.println("Parser error " + e);
		// System.out.println("failed on " + text);
		// e.printStackTrace();
            }
            
            return -1;
        }
        
        private long doGeneratingBenchmark(ArrayList<String> msgs, JsonInter p, int nbText) throws Exception {
            ArrayList<Object> objList = new ArrayList<Object>();
            Object obj = null;
            
            for (String json : msgs) {
                objList.add(p.parseObj(json));
            }
            
            try {
                long T1 = System.currentTimeMillis();
                
                for (int i = 0; i < loopCount; i++) {
                    obj = objList.get(i % nbText);
                    p.toJsonString(obj);
		}
		
                T1 = System.currentTimeMillis() - T1;
                
                System.out.println(BenchData.testMode + "> " + p.getSimpleName() + " : " + T1 + "ms");
                
		return T1;
            } catch (Exception e) {
                // System.out.println("Parser error " + e);
		// System.out.println("failed on " + text);
		// e.printStackTrace();
            }
            
            return -1;
        }

	private void showScript() throws IOException {
		File startBench = new File("runbench.bat");

		StringBuilder sb = new StringBuilder();
		sb.append("@echo off\r\n");
		sb.append("set CLASSPATH=");
		File dir = new File("lib");
		for (String s : dir.list()) {
			if (s.endsWith(".jar"))
				sb.append("lib").append(File.separator).append(s).append(';');
		}
		sb.append("bin");
		sb.append("\r\n");

		String[] allbench = "benchPreload|bench".split("\\|");
		String[] allTest = "text|unicode|float|int|mixte|boolean".split("\\|");

		for (int pass = 0; pass < 10; pass++) {
			sb.append("echo '**************'\r\n");
			sb.append("echo 'pass " + pass + "'\r\n");
			sb.append("echo '**************'\r\n");
			for (String bench : allbench) {
				System.out.println("bench : " + bench);
				for (String test : allTest) {
					for (int id = 0; id < 11; id++) {
						sb.append("java ").append(JsonBench.class.getName());
						sb.append(" ").append(bench);
						sb.append(" ").append(test);
						sb.append(" ").append(id);
						sb.append("\r\n");
					}
				}
			}
		}

		sb.append("java ").append(JsonBench.class.getName());
		sb.append(" showResult > result.wiki").append("\r\n");
		sb.append("pause\r\n");
		FileUtils.writeStringToFile(startBench, sb.toString());
	}

	private void formatResult() throws Exception {
		NumberFormat nf = NumberFormat.getPercentInstance();

		PrintStream ps = System.out;
		File file = new File("result.json");
		String text = FileUtils.readFileToString(file);
		JSONObject root = (JSONObject) JSONValue.parse(text);

		for (String bench : root.keySet()) {
			ps.println("");
			ps.println("== BenchMark : " + bench + "==");

			JSONObject allTest = (JSONObject) root.get(bench);
			Iterable<String> allApiName = getApiNames(allTest);
			Iterable<String> allTestName = allTest.keySet();

			TreeMap<String, Integer> total = new TreeMap<String, Integer>();
			TreeMap<String, Integer> bests = new TreeMap<String, Integer>();
			ArrayList<String> sortedApi = new ArrayList<String>();

			for (String api : allApiName) {
				int tot = 0;
				for (String test : allTestName) {
					JSONObject obj = (JSONObject) allTest.get(test);
					Number n = (Number) obj.get(api);

					if (n == null || n.intValue() == -1) {
						tot = -1;
						break;
					} else {
						tot += n.intValue();
						Number best = bests.get(test);
						if (best == null || best.intValue() > n.intValue())
							bests.put(test, n.intValue());
					}
				}
				total.put(api, tot);
				Number best = bests.get("total");
				if (tot > 0)
					if (best == null || best.intValue() > tot)
						bests.put("total", tot);

				String key_;
				api = tot + api;
				if (tot > 100000)
					key_ = api;
				else if (tot > 10000)
					key_ = "0" + api;
				else if (tot > 1000)
					key_ = "00" + api;
				else if (tot > 100)
					key_ = "000" + api;
				else if (tot > 10)
					key_ = "0000" + api;
				else if (tot == -1)
					key_ = "9000" + api;
				else
					key_ = "00000" + api;
				sortedApi.add(key_);
			}

			Collections.sort(sortedApi);
			Collections.reverse(sortedApi);
			for (int i = 0; i < sortedApi.size(); i++) {
				sortedApi.set(i, sortedApi.get(i).substring(6));
			}

			ps.print("|| Test || ");
			for (String testName : sortedApi) {
				ps.print(" *");
				ps.print(testName);
				ps.print("* ||");
			}
			ps.println("");

			for (String testName : allTestName) {
				ps.print("|| *");
				ps.print(testName);
				ps.print("* ||");
				JSONObject allApi = (JSONObject) allTest.get(testName);
				// for (String apiName : allApi.keySet()) {
				// }
				for (String apiName : sortedApi) {
					Number value = (Number) allApi.get(apiName);
					if (value == null || value.intValue() == -1)
						ps.print("N/A");
					else {
						ps.print(nf.format(value.floatValue() / bests.get(testName).floatValue()));
					}
					ps.print("||");
				}
				ps.println("");
			}

			ps.print("|| *TOTAL* ||");
			for (String apiName : sortedApi) {
				Number value = (Number) total.get(apiName);
				if (value == null || value.intValue() == -1)
					ps.print("N/A");
				else
					ps.print(nf.format(value.floatValue() / bests.get("total").floatValue()));
				// ps.print(nf.format(value));
				ps.print("||");
			}
			ps.println("");

		}
	}

	private Iterable<String> getApiNames(JSONObject obj) throws Exception {
		for (String k : obj.keySet()) {
			obj = (JSONObject) obj.get(k);
			return obj.keySet();
		}
		return null;
	}

	private void addResult(String bench, String testName, String apiName, long time) throws Exception {
		if (time == -1L)
			return;
		JSONObject root;
		File file = new File("result.json");

		if (file.exists()) {
			String text = FileUtils.readFileToString(file);
			root = (JSONObject) JSONValue.parse(text);
		} else {
			root = new JSONObject();
		}

		JSONObject current = root;
		// bench / benchPreload
		JSONObject tmp = (JSONObject) current.get(bench);
		if (tmp == null) {
			tmp = new JSONObject();
			current.put(bench, tmp);
		}
		current = tmp;

		// mixte, text, int, float ...
		tmp = (JSONObject) current.get(testName);
		if (tmp == null) {
			tmp = new JSONObject();
			current.put(testName, tmp);
		}
		current = tmp;

		// API name
		Number n = (Number) current.get(apiName);
		if (n == null || n.longValue() > time) {
			current.put(apiName, time);
			FileUtils.writeStringToFile(file, root.toJSONString());
			System.out.println("Score Updated");
		} else {
			System.out.println("Best : " + n);
		}

	}
}
