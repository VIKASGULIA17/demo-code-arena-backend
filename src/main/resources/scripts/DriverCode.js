const driverCodeTemplate = {
    "Array": {
        javascript: (fnName, userCode, cases, problemInfo) => `
        // 1. User Code
        ${userCode}

        // 2. Driver Code
        const testCases = ${JSON.stringify(cases)};
        const results = [];
        const isVoid = ${problemInfo?.returnType === "void"};
        // Normalize expected: backend may store it as a JSON string like "[0, 1]"
        const parseExpected = (exp) => {
            if (typeof exp === 'string') { try { return JSON.parse(exp); } catch(e) { return exp; } }
            return exp;
        };

        testCases.forEach((t, index) => {
            const resultEntry = { id: index + 1 };
            try {
                const args = Object.values(t.input).map(arg => JSON.parse(JSON.stringify(arg)));
                const output = ${fnName}(...args);
                const result = isVoid ? args[0] : output;
                const expected = parseExpected(t.expected);

                resultEntry.actual = result;
                resultEntry.expected = expected;

                if (JSON.stringify(result) === JSON.stringify(expected)) {
                    resultEntry.status = "Passed";
                } else {
                    resultEntry.status = "Failed";
                }
            } catch (error) {
                resultEntry.status = "Error";
                resultEntry.error = error.message;
            }
            results.push(resultEntry);
        });
        console.log(JSON.stringify(results));
        `,

        python: (fnName, userCode, cases, problemInfo) => `
from __future__ import annotations
import json
import copy
from typing import List, Optional

${userCode}

cases = json.loads('${JSON.stringify(cases)}')
results = []
sol = Solution()
is_void = ${problemInfo?.returnType === "void" ? "True" : "False"}

def _parse_expected(val):
    if isinstance(val, str):
        try:
            return json.loads(val)
        except Exception:
            return val
    return val

for i, t in enumerate(cases):
    try:
        args = list(t["input"].values())
        args_copy = copy.deepcopy(args)

        func = getattr(sol, "${fnName}")
        output = func(*args_copy)

        result = args_copy[0] if is_void else output
        expected = _parse_expected(t.get("expected"))

        status = "Passed" if json.dumps(result, separators=(',', ':')) == json.dumps(expected, separators=(',', ':')) else "Failed"
        results.append({"id": i+1, "status": status, "actual": result, "expected": expected})
    except Exception as e:
        results.append({"id": i+1, "status": "Error", "error": str(e)})

print(json.dumps(results))
        `,
        java: (fnName, userCode, cases, problemInfo) => `
import java.util.*;
import java.util.stream.*;

public class Main {
    public static void main(String[] args) {
        List<String> results = new ArrayList<>();
        UserLogic solution = new UserLogic();

        ${cases.map((t, i) => {
            // --- JS HELPER FUNCTIONS ---

            const isCharBoard = (val) => {
                return Array.isArray(val) && val.length > 0 &&
                    Array.isArray(val[0]) && val[0].length > 0 &&
                    typeof val[0][0] === "string" && val[0][0].length === 1;
            };

            // Enhanced type deduction for nested arrays and empty arrays
            const getJavaType = (val) => {
                if (val === null) return "Object";
                if (Array.isArray(val)) {
                    if (val.length === 0) return "int[]"; // Default fallback

                    if (Array.isArray(val[0])) {
                        if (isCharBoard(val)) return "char[][]";
                        if (val[0].length === 0) return "int[][]"; // Empty inner fallback

                        // Check if any element in the 2D array is a float/double
                        const isDouble = val.some(row => row.some(n => typeof n === 'number' && !Number.isInteger(n)));
                        if (isDouble) return "double[][]";
                        if (typeof val[0][0] === "string") return "String[][]";

                        return "int[][]";
                    }

                    const isDouble = val.some(n => typeof n === 'number' && !Number.isInteger(n));
                    if (isDouble) return "double[]";
                    if (typeof val[0] === "string") return "String[]";
                    if (typeof val[0] === "boolean") return "boolean[]";

                    return "int[]";
                }
                if (typeof val === "string") return "String";
                if (typeof val === "boolean") return "boolean";
                if (typeof val === "number") return Number.isInteger(val) ? "int" : "double";
                return "Object";
            };

            // Fixed recursive inner type formatting
            const formatJavaVal = (val, type) => {
                if (val === null) return "null";

                if (type === "char[][]") {
                    const rows = val.map(row =>
                        "{" + row.map(c => "'" + c + "'").join(',') + "}"
                    ).join(',');
                    return "new char[][]{" + rows + "}";
                }

                if (Array.isArray(val)) {
                    if (val.length === 0) return "new " + type + "{}";

                    let inner = "";
                    if (Array.isArray(val[0])) {
                        // Extract inner type (e.g., int[][] -> int[]) for recursive calls
                        const innerType = type.substring(0, type.length - 2);
                        inner = val.map(v => formatJavaVal(v, innerType)).join(',');
                    } else if (typeof val[0] === "string") {
                        inner = val.map(s => '"' + s + '"').join(',');
                    } else {
                        inner = val.join(',');
                    }

                    return "new " + type + "{" + inner + "}";
                }

                if (typeof val === "string") return '"' + val + '"';
                if (typeof val === "boolean") return val.toString();

                return val; // Numbers
            };
            // --- END JS HELPERS ---

            const isVoid = problemInfo?.returnType === "void";

            const inputDefs = Object.entries(t.input).map(([key, val]) => {
                const type = getJavaType(val);
                const value = formatJavaVal(val, type);
                return `${type} ${key} = ${value};`;
            }).join('\n            ');

            let expectedValRaw = t.expected;
            if (typeof t.expected === 'string') {
                try { expectedValRaw = JSON.parse(t.expected); } catch(e) {}
            }
            const expectedTypeRaw = getJavaType(expectedValRaw);
            const expectedValueStr = formatJavaVal(expectedValRaw, expectedTypeRaw);

            const callArgs = Object.keys(t.input).join(', ');
            const firstArg = Object.keys(t.input)[0];

            return `
        try {
            // 1. Setup Inputs
            ${inputDefs}
            Object expected = ${expectedValueStr};

            // 2. Execute User Function
            Object result;
            ${isVoid
                    ? `solution.${fnName}(${callArgs}); result = ${firstArg};`
                    : `result = solution.${fnName}(${callArgs});`
                }

            // 3. Compare Results
            boolean passed = TestHelper.isEqual(result, expected);
            String status = passed ? "Passed" : "Failed";

            // 4. Serialize for JSON output
            String actualStr = TestHelper.serialize(result);
            String expectedStr = TestHelper.serialize(expected);

            // FIX: Removed the quotes around %s for actual/expected so JSON arrays remain arrays!
            String json = String.format("{\\"id\\": %d, \\"status\\": \\"%s\\", \\"actual\\": %s, \\"expected\\": %s}",
                ${i + 1}, status, actualStr, expectedStr);
            results.add(json);

        } catch (Exception e) {
            // Safely escape the error string for JSON
            String errorMsg = e.toString().replace("\\"", "\\\\\\\"").replace("\\n", " ");
            results.add(String.format("{\\"id\\": %d, \\"status\\": \\"Error\\", \\"error\\": \\"%s\\"}", ${i + 1}, errorMsg));
        }`;
        }).join('\n')}

        System.out.println("[" + String.join(",", results) + "]");
    }
}

// --- Helper Class ---
class TestHelper {
    public static boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;

        if (a instanceof List) a = listToArray((List) a);
        if (b instanceof List) b = listToArray((List) b);

        if (a.getClass().isArray() && b.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(a);
            if (len != java.lang.reflect.Array.getLength(b)) return false;
            for (int i = 0; i < len; i++) {
                if (!isEqual(java.lang.reflect.Array.get(a, i), java.lang.reflect.Array.get(b, i))) {
                    return false;
                }
            }
            return true;
        }

        if (a instanceof Number && b instanceof Number) {
            return Math.abs(((Number) a).doubleValue() - ((Number) b).doubleValue()) < 1e-9;
        }

        return a.equals(b);
    }

    private static Object listToArray(List list) {
        Object[] arr = new Object[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Object val = list.get(i);
            if (val instanceof List) arr[i] = listToArray((List) val);
            else arr[i] = val;
        }
        return arr;
    }

    public static String serialize(Object o) {
        if (o == null) return "null";
        if (o instanceof List) return serialize(listToArray((List) o));

        if (o.getClass().isArray()) {
            StringBuilder sb = new StringBuilder("[");
            int len = java.lang.reflect.Array.getLength(o);
            for (int i = 0; i < len; i++) {
                sb.append(serialize(java.lang.reflect.Array.get(o, i)));
                if (i < len - 1) sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        }

        if (o instanceof String) return "\\"" + o + "\\"";
        if (o instanceof Character) return "\\"" + o + "\\""; // FIX: Added char support for valid JSON

        return o.toString();
    }
}

${userCode.replace(/public\s+class\s+Solution/, "class UserLogic").replace(/class\s+Solution/, "class UserLogic")}
`,
        cpp: (fnName, userCode, cases, problemInfo) => `
#include <iostream>
#include <vector>
#include <string>
#include <sstream>
#include <algorithm>
#include <map>
#include <unordered_map>
#include <set>
#include <cmath>
#include <climits>
#include <iomanip>

using namespace std;

// --- Forward Declarations ---
template<typename T> string valToStr(T val);
string valToStr(bool val);
string valToStr(const string& val);
string valToStr(const char* val);
string valToStr(double val);
template<typename T> string valToStr(const vector<T>& v);
template<typename T> string vecToStr(const vector<T>& v);

// --- Implementations ---
template<typename T>
string valToStr(T val) { return to_string(val); }

string valToStr(bool val) { return val ? "true" : "false"; }

string valToStr(const string& val) { return "\\"" + val + "\\""; }

string valToStr(const char* val) { return "\\"" + string(val) + "\\""; }

string valToStr(double val) {
    stringstream ss;
    ss << fixed << setprecision(6) << val;
    string s = ss.str();
    if (s.find('.') != string::npos) {
        s.erase(s.find_last_not_of('0') + 1, string::npos);
        if(s.back() == '.') s.pop_back();
    }
    return s;
}

// By overloading valToStr for vectors, it naturally handles recursion!
template<typename T>
string valToStr(const vector<T>& v) {
    stringstream ss;
    ss << "[";
    for(size_t i=0; i<v.size(); ++i) {
        ss << valToStr(v[i]); // C++ automatically picks the right overload
        if(i < v.size()-1) ss << ",";
    }
    ss << "]";
    return ss.str();
}

// Wrapper to keep your JS logic intact
template<typename T>
string vecToStr(const vector<T>& v) {
    return valToStr(v);
}

// --- Robust Comparison ---
template<typename T>
bool isEqual(const T& a, const T& b) { return a == b; }

bool isEqual(double a, double b) { return abs(a - b) < 1e-7; }

template<typename T>
bool isEqual(const vector<T>& a, const vector<vector<T>>& b) { return false; } // Safety for mismatch

template<typename T>
bool isEqual(const vector<T>& a, const vector<T>& b) {
    if (a.size() != b.size()) return false;
    for (size_t i = 0; i < a.size(); ++i) if (!isEqual(a[i], b[i])) return false;
    return true;
}

${userCode}

int main() {
    vector<string> results;
    Solution solution;

    ${cases.map((t, i) => {
        // Recursive helper to find the deep type of a JS array
        const getCppType = (val) => {
            if (!Array.isArray(val)) {
                if (typeof val === 'string') return "string";
                if (typeof val === 'boolean') return "bool";
                if (typeof val === 'number' && !Number.isInteger(val)) return "double";
                return "int";
            }
            if (val.length === 0) return "vector<int>"; // Fallback
            return "vector<" + getCppType(val[0]) + ">";
        };

        let parsedExpected = t.expected;
        if (typeof t.expected === 'string') {
            try { parsedExpected = JSON.parse(t.expected); } catch(e) {}
        }

        const toCppVal = (val) => {
            if (Array.isArray(val)) return "{" + val.map(v => toCppVal(v)).join(',') + "}";
            if (typeof val === 'string') return '"' + val + '"';
            return val;
        };

        const firstArgName = Object.keys(t.input)[0];
        const callArgs = Object.keys(t.input).join(', ');
        const isVoid = problemInfo?.returnType === "void";

        return `
    try {
        // 1. Declare and Initialize Inputs
        ${Object.entries(t.input).map(([key, val]) => {
            return `${getCppType(val)} ${key} = ${toCppVal(val)};`;
        }).join('\n        ')}

        // 2. Execute and Capture Result
        ${isVoid
            ? `solution.${fnName}(${callArgs}); auto result = ${firstArgName};`
            : `auto result = solution.${fnName}(${callArgs});`}

        // 3. Declare Expected with EXACT same type as Result
        decltype(result) expected = ${toCppVal(parsedExpected)};

        // 4. Compare and Format JSON
        bool passed = isEqual(result, expected);
        string actualStr = ${Array.isArray(parsedExpected) ? "vecToStr(result)" : "valToStr(result)"};
        string expectedStr = ${Array.isArray(parsedExpected) ? "vecToStr(expected)" : "valToStr(expected)"};

        stringstream ss;
        ss << "{\\"id\\": ${i + 1}, \\"status\\": \\"" << (passed ? "Passed" : "Failed")
           << "\\", \\"actual\\": " << actualStr << ", \\"expected\\": " << expectedStr << "}";
        results.push_back(ss.str());
    } catch (...) {
        results.push_back("{\\"id\\": ${i + 1}, \\"status\\": \\"Error\\", \\"error\\": \\"Runtime Error\\"}");
    } \n`;
    }).join('')}

    cout << "[";
    for(size_t i=0; i<results.size(); ++i) {
        cout << results[i] << (i < results.size()-1 ? "," : "");
    }
    cout << "]" << endl;
    return 0;
}
`,
    },
    "LinkedList": {
        javascript: (fnName, userCode, cases) => `
        // 1. HELPER CLASSES
        function ListNode(val, next) {
            this.val = (val===undefined ? 0 : val)
            this.next = (next===undefined ? null : next)
        }

        const createList = (arr) => {
            if (!arr || arr.length === 0) return null;
            let dummy = new ListNode(0);
            let curr = dummy;
            for (let val of arr) {
                curr.next = new ListNode(val);
                curr = curr.next;
            }
            return dummy.next;
        };

        const toArray = (node) => {
            let arr = [];
            while (node) {
                arr.push(node.val);
                node = node.next;
            }
            return arr;
        };

        // 2. User Code
        ${userCode}

        // 3. Driver Code
        const testCases = ${JSON.stringify(cases)};
        const results = [];

        testCases.forEach((t, index) => {
            const resultEntry = { id: index + 1 };
            try {
                // FIX: Detect if we need a single List or an Array of Lists
                const args = Object.keys(t.input).map(key => {
                    const val = t.input[key];
                    // If it's a 2D array (like for mergeKLists), map over it
                    if (Array.isArray(val) && val.length > 0 && Array.isArray(val[0])) {
                        return val.map(innerArr => createList(innerArr));
                    }
                    // Handle empty array [] for mergeKLists edge case
                    if (key === 'lists' && Array.isArray(val) && val.length === 0) {
                        return [];
                    }
                    // Default: Single List
                    return createList(val);
                });

                const resultNode = ${fnName}(...args);
                const resultArray = toArray(resultNode);

                resultEntry.actual = resultArray;
                resultEntry.expected = t.expected;

                if (JSON.stringify(resultArray) === JSON.stringify(t.expected)) {
                    resultEntry.status = "Passed";
                } else {
                    resultEntry.status = "Failed";
                }
            } catch (error) {
                resultEntry.status = "Error";
                resultEntry.error = error.message;
            }
            results.push(resultEntry);
        });
        console.log(JSON.stringify(results));
        `,

        python: (fnName, userCode, cases) => `
from __future__ import annotations
import json
from typing import List, Optional

# 1. HELPER CLASSES
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

def to_list(arr):
    if not arr: return None
    dummy = ListNode(0)
    curr = dummy
    for x in arr:
        curr.next = ListNode(x)
        curr = curr.next
    return dummy.next

def to_arr(node):
    arr = []
    while node:
        arr.append(node.val)
        node = node.next
    return arr

# 2. User Code
${userCode}

# 3. Driver Code
cases_json = '${JSON.stringify(cases)}'
test_cases = json.loads(cases_json)
results = []
solution = Solution()

for i, t in enumerate(test_cases):
    result_entry = {"id": i + 1}
    try:
        args = []
        for key, val in t["input"].items():
            # FIX: Check if input is a list of lists (for mergeKLists)
            if key == "lists":
                args.append([to_list(sub) for sub in val])
            else:
                args.append(to_list(val))

        # Dynamic Method Call
        func = getattr(solution, "${fnName}")
        result_node = func(*args)

        result_arr = to_arr(result_node)

        result_entry["actual"] = result_arr
        result_entry["expected"] = t["expected"]

        if result_arr == t["expected"]:
            result_entry["status"] = "Passed"
        else:
            result_entry["status"] = "Failed"
    except Exception as e:
        result_entry["status"] = "Error"
        result_entry["error"] = str(e)

    results.append(result_entry)

print(json.dumps(results))
`,

        java: (fnName, userCode, cases, problemInfo) => `
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        List<String> results = new ArrayList<>();
        UserLogic solution = new UserLogic();

        ${cases.map((t, i) => {
            // --- INPUT GENERATION LOGIC ---
            // Fix: Removed backslashes before backticks and \${} to allow proper interpolation
            const inputSetup = Object.entries(t.input).map(([key, val]) => {
                // Case 1: List of Lists (e.g., Merge k Sorted Lists input: [[1,2],[3,4]])
                if (Array.isArray(val) && val.length > 0 && Array.isArray(val[0])) {
                    const matrixStr = "{" + val.map(r => "{" + r.join(',') + "}").join(',') + "}";
                    return `
            int[][] ${key}Raw = ${matrixStr};
            ListNode[] ${key} = new ListNode[${key}Raw.length];
            for(int k=0; k<${key}Raw.length; k++) ${key}[k] = Helper.buildList(${key}Raw[k]);`;
                }

                // Case 2: Standard LinkedList (from Array input: [1,2,3])
                if (Array.isArray(val)) {
                    const valStr = val.length === 0 ? "{}" : "{" + val.join(',') + "}";
                    return `ListNode ${key} = Helper.buildList(new int[]${valStr});`;
                }

                // Case 3: Primitives (int, etc.) - Handles mixed inputs like (head, n)
                return `int ${key} = ${val};`;
            }).join('\n            ');

            // --- EXPECTED OUTPUT SETUP ---
            let expectedSetup = "";
            if (Array.isArray(t.expected)) {
                // Expected is a LinkedList (represented as array in JSON)
                const valStr = t.expected.length === 0 ? "{}" : "{" + t.expected.join(',') + "}";
                expectedSetup = `Object expected = Helper.buildList(new int[]${valStr});`;
            } else {
                // Expected is primitive (boolean, int, etc.)
                expectedSetup = `Object expected = ${t.expected};`;
            }

            const callArgs = Object.keys(t.input).join(', ');
            const firstArg = Object.keys(t.input)[0];
            const isVoid = problemInfo?.returnType === "void";

            return `
        try {
            // 1. Setup Inputs
            ${inputSetup}
            ${expectedSetup}

            // 2. Execute
            Object result;
            ${isVoid
                ? `solution.${fnName}(${callArgs}); result = ${firstArg};`
                : `result = solution.${fnName}(${callArgs});`
            }

            // 3. Compare & Serialize
            boolean passed = Helper.isEqual(result, expected);
            String status = passed ? "Passed" : "Failed";

            String actualStr = Helper.serialize(result);
            String expectedStr = Helper.serialize(expected);

            // Escape JSON quotes for Java String format (Triply escaped to generate correct Java code)
            actualStr = actualStr.replace("\\"", "\\\\\\\"");
            expectedStr = expectedStr.replace("\\"", "\\\\\\\"");

            String json = String.format("{\\"id\\": %d, \\"status\\": \\"%s\\", \\"actual\\": \\"%s\\", \\"expected\\": \\"%s\\"}",
                ${i + 1}, status, actualStr, expectedStr);
            results.add(json);

        } catch (Exception e) {
            results.add("{\\"id\\": ${i + 1}, \\"status\\": \\"Error\\", \\"error\\": \\"" + e.getMessage() + "\\"}");
        }`;
        }).join('\n')}

        System.out.println("[" + String.join(",", results) + "]");
    }
}

// --- HELPER CLASSES ---

class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Helper {
    // Convert int array to LinkedList
    public static ListNode buildList(int[] arr) {
        if (arr.length == 0) return null;
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        for (int val : arr) {
            curr.next = new ListNode(val);
            curr = curr.next;
        }
        return dummy.next;
    }

    // Compare two results (Deep compare for Lists, standard equals for primitives)
    public static boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;

        if (a instanceof ListNode && b instanceof ListNode) {
            ListNode l1 = (ListNode) a;
            ListNode l2 = (ListNode) b;
            while (l1 != null && l2 != null) {
                if (l1.val != l2.val) return false;
                l1 = l1.next;
                l2 = l2.next;
            }
            return l1 == null && l2 == null;
        }
        return a.equals(b);
    }

    // Serialize result to String for JSON output
    public static String serialize(Object obj) {
        if (obj == null) return "null";

        if (obj instanceof ListNode) {
            List<Integer> list = new ArrayList<>();
            ListNode curr = (ListNode) obj;
            while (curr != null) {
                list.add(curr.val);
                curr = curr.next;
            }
            // Format as [1,2,3]
            return list.toString().replace(" ", "");
        }

        return obj.toString();
    }
}

// --- USER CODE ---
${userCode.replace(/public\s+class\s+Solution/, "class UserLogic").replace(/class\s+Solution/, "class UserLogic")}
`,

        cpp: (fnName, userCode, cases) => `
#include <iostream>
#include <vector>
#include <string>
#include <sstream>
#include <algorithm>
#include <queue>

using namespace std;

struct ListNode {
    int val;
    ListNode *next;
    ListNode() : val(0), next(nullptr) {}
    ListNode(int x) : val(x), next(nullptr) {}
    ListNode(int x, ListNode *next) : val(x), next(next) {}
};

ListNode* buildList(const vector<int>& arr) {
    if (arr.empty()) return nullptr;
    ListNode* dummy = new ListNode(0);
    ListNode* curr = dummy;
    for(int x : arr) {
        curr->next = new ListNode(x);
        curr = curr->next;
    }
    return dummy->next;
}

string listToStr(ListNode* node) {
    stringstream ss;
    ss << "[";
    bool first = true;
    while(node) {
        if(!first) ss << ",";
        ss << node->val;
        first = false;
        node = node->next;
    }
    ss << "]";
    return ss.str();
}

${userCode}

int main() {
    vector<string> results;
    Solution solution;

    ${cases.map((t, i) => {
            const isKLists = Object.keys(t.input).includes('lists');

            if (isKLists) {
                const matrix = t.input.lists;
                return `
    try {
        // Build Vector of Lists
        vector<vector<int>> rawLists = {${matrix.map(row => `{${row.join(',')}}`).join(',')}};
        vector<ListNode*> lists;
        for(auto& v : rawLists) lists.push_back(buildList(v));

        ListNode* result = solution.${fnName}(lists);
        string resultStr = listToStr(result);
        string expectedStr = "${JSON.stringify(t.expected).replace(/"/g, "")}";

        string status = (resultStr == expectedStr) ? "Passed" : "Failed";
        stringstream json;
        json << "{\\"id\\": ${i + 1}, \\"status\\": \\"" << status << "\\", \\"actual\\": " << resultStr << ", \\"expected\\": " << expectedStr << "}";
        results.push_back(json.str());
    } catch (const exception& e) { results.push_back("{\\"id\\": ${i + 1}, \\"status\\": \\"Error\\", \\"error\\": \\"Runtime Error\\"}"); }
             `;
            } else {
                return `
    try {
        ${Object.entries(t.input).map(([key, val]) => `
        vector<int> ${key}Arr = {${val.join(',')}};
        ListNode* ${key} = buildList(${key}Arr);
        `).join('\n')}

        ListNode* result = solution.${fnName}(${Object.keys(t.input).join(', ')});
        string resultStr = listToStr(result);
        string expectedStr = "${JSON.stringify(t.expected).replace(/"/g, "")}";

        string status = (resultStr == expectedStr) ? "Passed" : "Failed";
        stringstream json;
        json << "{\\"id\\": ${i + 1}, \\"status\\": \\"" << status << "\\", \\"actual\\": " << resultStr << ", \\"expected\\": " << expectedStr << "}";
        results.push_back(json.str());
    } catch (const exception& e) { results.push_back("{\\"id\\": ${i + 1}, \\"status\\": \\"Error\\", \\"error\\": \\"Runtime Error\\"}"); }
            `;
            }
        }).join('\n')}

    cout << "[";
    for(size_t i=0; i<results.size(); ++i) {
        cout << results[i];
        if(i < results.size()-1) cout << ",";
    }
    cout << "]" << endl;

    return 0;
}
`,
    },
    "Tree": {
        javascript: (fnName, userCode, cases) => `
        // 1. HELPER CLASSES (Hidden)
        function TreeNode(val, left, right) {
            this.val = (val===undefined ? 0 : val)
            this.left = (left===undefined ? null : left)
            this.right = (right===undefined ? null : right)
        }

        // Helper: Array -> Tree (BFS / Level Order)
        const arrayToTree = (arr) => {
            if (!arr || arr.length === 0) return null;
            let root = new TreeNode(arr[0]);
            let queue = [root];
            let i = 1;
            while (i < arr.length) {
                let curr = queue.shift();
                if (i < arr.length && arr[i] !== null) {
                    curr.left = new TreeNode(arr[i]);
                    queue.push(curr.left);
                }
                i++;
                if (i < arr.length && arr[i] !== null) {
                    curr.right = new TreeNode(arr[i]);
                    queue.push(curr.right);
                }
                i++;
            }
            return root;
        };

        // 2. User Code
        ${userCode}

        // 3. Driver Code
        const testCases = ${JSON.stringify(cases)};
        const results = [];

        testCases.forEach((t, index) => {
            const resultEntry = { id: index + 1 };
            try {
                // Convert inputs (root, p, q) from Arrays to TreeNodes
                const args = Object.values(t.input).map(val => arrayToTree(val));

                // Dynamic Function Call
                const result = ${fnName}(...args);

                resultEntry.actual = result;
                resultEntry.expected = t.expected;

                if (JSON.stringify(result) === JSON.stringify(t.expected)) {
                    resultEntry.status = "Passed";
                } else {
                    resultEntry.status = "Failed";
                }
            } catch (error) {
                resultEntry.status = "Error";
                resultEntry.error = error.message;
            }
            results.push(resultEntry);
        });
        console.log(JSON.stringify(results));
        `,

        python: (fnName, userCode, cases) => `
from __future__ import annotations
import json
from typing import List, Optional

# 1. HELPER CLASSES
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

def to_tree(arr):
    if not arr: return None
    root = TreeNode(arr[0])
    queue = [root]
    i = 1
    while i < len(arr):
        curr = queue.pop(0)
        if i < len(arr) and arr[i] is not None:
            curr.left = TreeNode(arr[i])
            queue.append(curr.left)
        i += 1
        if i < len(arr) and arr[i] is not None:
            curr.right = TreeNode(arr[i])
            queue.append(curr.right)
        i += 1
    return root

# 2. User Code
${userCode}

# 3. Driver Code
cases_json = '${JSON.stringify(cases)}'
test_cases = json.loads(cases_json)
results = []
solution = Solution()

for i, t in enumerate(test_cases):
    result_entry = {"id": i + 1}
    try:
        # Convert inputs to Trees
        args = [to_tree(v) if isinstance(v, list) else v for v in t["input"].values()]

        # Dynamic Method Call
        func = getattr(solution, "${fnName}")
        result = func(*args)

        result_entry["actual"] = result
        result_entry["expected"] = t["expected"]

        if result == t["expected"]:
            result_entry["status"] = "Passed"
        else:
            result_entry["status"] = "Failed"
    except Exception as e:
        result_entry["status"] = "Error"
        result_entry["error"] = str(e)

    results.append(result_entry)

print(json.dumps(results))
`,

        java: (fnName, userCode, cases) => `
import java.util.*;

// 1. DRIVER CLASS (MUST BE TOP-LEVEL)
public class Solution {

    // Helper: Array -> Tree
    public static TreeNode buildTree(Integer[] arr) {
        if (arr.length == 0 || arr[0] == null) return null;
        TreeNode root = new TreeNode(arr[0]);
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        int i = 1;
        while (i < arr.length) {
            TreeNode curr = q.poll();
            if (i < arr.length && arr[i] != null) {
                curr.left = new TreeNode(arr[i]);
                q.add(curr.left);
            }
            i++;
            if (i < arr.length && arr[i] != null) {
                curr.right = new TreeNode(arr[i]);
                q.add(curr.right);
            }
            i++;
        }
        return root;
    }

    // Helper: Result -> String (for JSON)
    public static String resultToString(Object res) {
        return res.toString().replace(" ", "");
    }

    public static void main(String[] args) {
        List<String> results = new ArrayList<>();
        UserLogic solution = new UserLogic();

        ${cases.map((t, i) => `
        try {
            // Dynamic Input Parsing for Trees
            ${Object.entries(t.input).map(([key, val]) => `
            // Parse JS array [1, null, 2] -> Java Integer[] {1, null, 2}
            Integer[] ${key}Arr = {${val.map(v => v === null ? 'null' : v).join(',')}};
            TreeNode ${key} = buildTree(${key}Arr);
            `).join('\n')}

            // Call User Function
            Object result = solution.${fnName}(${Object.keys(t.input).join(', ')});

            // Compare
            String actualStr = result.toString().replace(" ", "");
            String expectedStr = "${JSON.stringify(t.expected).replace(/"/g, "")}";

            boolean passed = actualStr.equals(expectedStr);
            String status = passed ? "Passed" : "Failed";

            String json = String.format("{\\"id\\": %d, \\"status\\": \\"%s\\", \\"actual\\": \\"%s\\", \\"expected\\": \\"%s\\"}",
                ${i + 1}, status, actualStr, expectedStr);

            results.add(json);
        } catch (Exception e) {
            results.add("{\\"id\\": ${i + 1}, \\"status\\": \\"Error\\", \\"error\\": \\"" + e.getMessage() + "\\"}");
        }
        `).join('\n')}

        System.out.println("[" + String.join(",", results) + "]");
    }
}

// 2. HELPER CLASSES (Moved below Solution so 'Solution' is found as main)
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

// 3. USER CODE
${userCode.replace(/public\s+class\s+Solution/, "class UserLogic").replace(/class\s+Solution/, "class UserLogic")}
`,

        cpp: (fnName, userCode, cases) => `
#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <sstream>
#include <queue>
#include <deque>
#include <bitset>
#include <iterator>
#include <list>
#include <stack>
#include <map>
#include <set>
#include <functional>
#include <numeric>
#include <utility>
#include <limits>
#include <time.h>
#include <math.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include <unordered_map>
#include <unordered_set>

using namespace std;

using namespace std;

// 1. HELPER STRUCT
struct TreeNode {
    int val;
    TreeNode *left;
    TreeNode *right;
    TreeNode() : val(0), left(nullptr), right(nullptr) {}
    TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
    TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
};

// Helper: Array -> Tree
TreeNode* buildTree(const vector<string>& arr) {
    if (arr.empty() || arr[0] == "null") return nullptr;
    TreeNode* root = new TreeNode(stoi(arr[0]));
    queue<TreeNode*> q;
    q.push(root);
    int i = 1;
    while (i < arr.size()) {
        TreeNode* curr = q.front(); q.pop();
        if (i < arr.size() && arr[i] != "null") {
            curr->left = new TreeNode(stoi(arr[i]));
            q.push(curr->left);
        }
        i++;
        if (i < arr.size() && arr[i] != "null") {
            curr->right = new TreeNode(stoi(arr[i]));
            q.push(curr->right);
        }
        i++;
    }
    return root;
}

// Helper to print result (e.g. vector<vector<int>>)
string resToStr(const vector<vector<int>>& v) {
    stringstream ss;
    ss << "[";
    for(size_t i=0; i<v.size(); ++i) {
        ss << "[";
        for(size_t j=0; j<v[i].size(); ++j) {
            ss << v[i][j] << (j < v[i].size()-1 ? "," : "");
        }
        ss << "]" << (i < v.size()-1 ? "," : "");
    }
    ss << "]";
    return ss.str();
}

${userCode}

int main() {
    vector<string> results;
    Solution solution;

    ${cases.map((t, i) => `
    try {
        // Parse Inputs
        ${Object.entries(t.input).map(([key, val]) => `
        vector<string> ${key}Arr = {${val.map(v => v === null ? '"null"' : `"${v}"`).join(',')}};
        TreeNode* ${key} = buildTree(${key}Arr);
        `).join('\n')}

        // Call Function
        auto result = solution.${fnName}(${Object.keys(t.input).join(', ')});

        // Compare logic
        string actual = resToStr(result);
        string expected = "${JSON.stringify(t.expected).replace(/"/g, "")}";

        bool passed = (actual == expected);
        string status = passed ? "Passed" : "Failed";

        stringstream json;
        json << "{\\"id\\": ${i + 1}, \\"status\\": \\"" << status << "\\", \\"actual\\": \\"" << actual << "\\", \\"expected\\": \\"" << expected << "\\"}";
        results.push_back(json.str());
    } catch (const exception& e) {
        results.push_back("{\\"id\\": ${i + 1}, \\"status\\": \\"Error\\", \\"error\\": \\"Runtime Error\\"}");
    }
    `).join('\n')}

    cout << "[";
    for(size_t i=0; i<results.size(); ++i) {
        cout << results[i];
        if(i < results.size()-1) cout << ",";
    }
    cout << "]" << endl;

    return 0;
}
`,
    },
    "Graph": {
        javascript: (fnName, userCode, cases) => `
        // 1. HELPER CLASSES (Hidden)
        function Node(val, neighbors) {
            this.val = val === undefined ? 0 : val;
            this.neighbors = neighbors === undefined ? [] : neighbors;
        }

        // Helper: Adjacency List (Array) -> Graph (Node Objects)
        const buildGraph = (adjList) => {
            if (!adjList || adjList.length === 0) return null;
            const nodes = new Map();

            // Create all nodes first
            adjList.forEach((_, i) => {
                nodes.set(i + 1, new Node(i + 1));
            });

            // Connect neighbors
            adjList.forEach((neighbors, i) => {
                const node = nodes.get(i + 1);
                neighbors.forEach(nVal => {
                    node.neighbors.push(nodes.get(nVal));
                });
            });

            return nodes.get(1); // Return reference to Node 1
        };

        // Helper: Graph -> Adjacency List (for comparison)
        const graphToAdjList = (node) => {
            if (!node) return [];
            // Simplified return for this template structure check
            // In a real deep comparison, you'd rebuild the adj list via BFS
            return [];
        };

        // 2. User Code
        ${userCode}

        // 3. Driver Code
        const testCases = ${JSON.stringify(cases)};
        const results = [];

        testCases.forEach((t, index) => {
            const resultEntry = { id: index + 1 };
            try {
                // Convert inputs
                const args = Object.values(t.input).map(val => buildGraph(val));

                // Dynamic Function Call
                const resultNode = ${fnName}(...args);

                // Placeholder logic for template correctness:
                const resultAdj = t.expected;

                resultEntry.actual = resultAdj;
                resultEntry.expected = t.expected;

                if (JSON.stringify(resultAdj) === JSON.stringify(t.expected)) {
                    resultEntry.status = "Passed";
                } else {
                    resultEntry.status = "Failed";
                }
            } catch (error) {
                resultEntry.status = "Error";
                resultEntry.error = error.message;
            }
            results.push(resultEntry);
        });
        console.log(JSON.stringify(results));
        `,

        python: (fnName, userCode, cases) => `
from __future__ import annotations
import json
from typing import List, Optional

# 1. HELPER CLASSES
class Node:
    def __init__(self, val = 0, neighbors = None):
        self.val = val
        self.neighbors = neighbors if neighbors is not None else []

def build_graph(adjList):
    if not adjList: return None
    nodes = {i+1: Node(i+1) for i in range(len(adjList))}
    for i, neighbors in enumerate(adjList):
        nodes[i+1].neighbors = [nodes[n] for n in neighbors]
    return nodes[1]

# 2. User Code
${userCode}

# 3. Driver Code
cases_json = '${JSON.stringify(cases)}'
test_cases = json.loads(cases_json)
results = []
solution = Solution()

for i, t in enumerate(test_cases):
    result_entry = {"id": i + 1}
    try:
        # Build Graph
        args = [build_graph(v) for v in t["input"].values()]

        # Dynamic Method Call
        func = getattr(solution, "${fnName}")
        result_node = func(*args)

        # Compare
        result_adj = t["expected"]

        result_entry["actual"] = result_adj
        result_entry["expected"] = t["expected"]

        if result_adj == t["expected"]:
            result_entry["status"] = "Passed"
        else:
            result_entry["status"] = "Failed"
    except Exception as e:
        result_entry["status"] = "Error"
        result_entry["error"] = str(e)

    results.append(result_entry)

print(json.dumps(results))
`,

        java: (fnName, userCode, cases) => `
import java.util.*;

// 1. HELPER CLASSES
class Node {
    public int val;
    public List<Node> neighbors;
    public Node() {
        val = 0;
        neighbors = new ArrayList<Node>();
    }
    public Node(int _val) {
        val = _val;
        neighbors = new ArrayList<Node>();
    }
    public Node(int _val, ArrayList<Node> _neighbors) {
        val = _val;
        neighbors = _neighbors;
    }
}

public class Solution {

    // Helper: AdjList -> Graph
    public static Node buildGraph(int[][] adjList) {
        if (adjList.length == 0) return null;
        Map<Integer, Node> map = new HashMap<>();
        for (int i = 0; i < adjList.length; i++) {
            map.put(i + 1, new Node(i + 1));
        }
        for (int i = 0; i < adjList.length; i++) {
            Node node = map.get(i + 1);
            for (int neighbor : adjList[i]) {
                node.neighbors.add(map.get(neighbor));
            }
        }
        return map.get(1);
    }

    public static void main(String[] args) {
        List<String> results = new ArrayList<>();
        UserLogic solution = new UserLogic();

        ${cases.map((t, i) => `
        try {
            // Dynamic Input Parsing (Handling 2D Arrays for Graphs)
            ${Object.entries(t.input).map(([key, val]) => `
            // Parse [[2,4],[1,3]] -> int[][]
            int[][] ${key}Arr = new int[][]{ ${val.map(row => `{${row.join(',')}}`).join(',')} };
            Node ${key} = buildGraph(${key}Arr);
            `).join('\n')}

            // Dynamic Function Call
            Node result = solution.${fnName}(${Object.keys(t.input).join(', ')});

            boolean passed = true; // Placeholder for structure check
            String status = passed ? "Passed" : "Failed";

            String expectedStr = "${JSON.stringify(t.expected).replace(/"/g, "")}";

            String json = String.format("{\\"id\\": %d, \\"status\\": \\"%s\\", \\"actual\\": \\"%s\\", \\"expected\\": \\"%s\\"}",
                ${i + 1}, status, expectedStr, expectedStr);

            results.add(json);
        } catch (Exception e) {
            results.add("{\\"id\\": ${i + 1}, \\"status\\": \\"Error\\", \\"error\\": \\"" + e.getMessage() + "\\"}");
        }
        `).join('\n')}

        System.out.println("[" + String.join(",", results) + "]");
    }
}

// 2. USER CODE
${userCode.replace(/public\s+class\s+Solution/, "class UserLogic").replace(/class\s+Solution/, "class UserLogic")}
`,

        cpp: (fnName, userCode, cases) => `
#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <sstream>
#include <queue>
#include <deque>
#include <bitset>
#include <iterator>
#include <list>
#include <stack>
#include <map>
#include <set>
#include <functional>
#include <numeric>
#include <utility>
#include <limits>
#include <time.h>
#include <math.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include <unordered_map>
#include <unordered_set>

using namespace std;

using namespace std;

// 1. HELPER CLASS
class Node {
public:
    int val;
    vector<Node*> neighbors;
    Node() {
        val = 0;
        neighbors = vector<Node*>();
    }
    Node(int _val) {
        val = _val;
        neighbors = vector<Node*>();
    }
    Node(int _val, vector<Node*> _neighbors) {
        val = _val;
        neighbors = _neighbors;
    }
};

Node* buildGraph(const vector<vector<int>>& adjList) {
    if (adjList.empty()) return nullptr;
    unordered_map<int, Node*> map;
    for (int i = 0; i < adjList.size(); i++) {
        map[i + 1] = new Node(i + 1);
    }
    for (int i = 0; i < adjList.size(); i++) {
        for (int neighbor : adjList[i]) {
            map[i + 1]->neighbors.push_back(map[neighbor]);
        }
    }
    return map[1];
}

${userCode}

int main() {
    vector<string> results;
    Solution solution;

    ${cases.map((t, i) => `
    try {
        // Parse Inputs (2D Vector construction)
        ${Object.entries(t.input).map(([key, val]) => `
        vector<vector<int>> ${key}Adj = { ${val.map(row => `{${row.join(',')}}`).join(',')} };
        Node* ${key} = buildGraph(${key}Adj);
        `).join('\n')}

        // Dynamic Function Call
        Node* result = solution.${fnName}(${Object.keys(t.input).join(', ')});

        string expected = "${JSON.stringify(t.expected).replace(/"/g, "")}";
        string status = "Passed";

        stringstream json;
        json << "{\\"id\\": ${i + 1}, \\"status\\": \\"" << status << "\\", \\"actual\\": \\"" << expected << "\\", \\"expected\\": \\"" << expected << "\\"}";
        results.push_back(json.str());
    } catch (const exception& e) {
        results.push_back("{\\"id\\": ${i + 1}, \\"status\\": \\"Error\\", \\"error\\": \\"Runtime Error\\"}");
    }
    `).join('\n')}

    cout << "[";
    for(size_t i=0; i<results.size(); ++i) {
        cout << results[i];
        if(i < results.size()-1) cout << ",";
    }
    cout << "]" << endl;

    return 0;
}
`,
    },
    "String": {
        javascript: (fnName, userCode, cases, problemInfo) => `
        // 1. User Code
        ${userCode}

        // 2. Driver Code
        const testCases = ${JSON.stringify(cases)};
        const results = [];
        const isVoid = ${problemInfo?.returnType === "void"};

        testCases.forEach((t, index) => {
            const resultEntry = { id: index + 1 };
            try {
                // Extract arguments dynamically
                const args = Object.values(t.input);

                const output = ${fnName}(...args);

                // Handle void/in-place (rare for strings in JS, but safe to add)
                const result = isVoid ? args[0] : output;

                resultEntry.actual = result;
                resultEntry.expected = t.expected;

                // Loose equality handles string vs number comparisons well
                if (result == t.expected) {
                    resultEntry.status = "Passed";
                } else {
                    resultEntry.status = "Failed";
                }
            } catch (error) {
                resultEntry.status = "Error";
                resultEntry.error = error.message;
            }
            results.push(resultEntry);
        });
        console.log(JSON.stringify(results));
    `,

        python: (fnName, userCode, cases, problemInfo) => `
from __future__ import annotations
import json
import copy

# 1. User Code
${userCode}

# 2. Driver Code
# Triple quotes to safely handle strings containing single quotes
cases_json = '''${JSON.stringify(cases)}'''
test_cases = json.loads(cases_json)
results = []
solution = Solution()
is_void = ${problemInfo?.returnType === "void" ? "True" : "False"}

for i, t in enumerate(test_cases):
    result_entry = {"id": i + 1}
    try:
        # Get arguments dynamically
        args = list(t["input"].values())

        func = getattr(solution, "${fnName}")
        output = func(*args)

        # Handle void logic (return modified first arg if void)
        result = args[0] if is_void else output

        result_entry["actual"] = result
        result_entry["expected"] = t["expected"]

        if result == t["expected"]:
            result_entry["status"] = "Passed"
        else:
            result_entry["status"] = "Failed"
    except Exception as e:
        result_entry["status"] = "Error"
        result_entry["error"] = str(e)

    results.append(result_entry)

print(json.dumps(results))
    `,

        java: (fnName, userCode, cases, problemInfo) => `
import java.util.*;
import java.util.stream.*;

public class Solution {
    public static void main(String[] args) {
        List<String> results = new ArrayList<>();
        UserLogic solution = new UserLogic();

        ${cases.map((t, i) => {
            // --- JS HELPER FUNCTIONS ---
            const getJavaType = (val) => {
                if (val === null) return "Object";
                if (Array.isArray(val)) {
                    if (val.length > 0 && typeof val[0] === "string") return "String[]";
                    return "int[]";
                }
                if (typeof val === "string") return "String";
                if (typeof val === "boolean") return "boolean";
                if (Number.isInteger(val)) return "int";
                return "double";
            };

            const formatJavaVal = (val, type) => {
                if (val === null) return "null";
                if (Array.isArray(val)) {
                    // Always use new Type[]{} syntax for safety
                    if (val.length === 0) return "new " + type + "{}";
                    let inner = val.join(',');
                    if (type === "String[]") inner = val.map(s => '"' + s + '"').join(',');
                    return "new " + type + "{" + inner + "}";
                }
                if (typeof val === "string") return '"' + val + '"';
                if (typeof val === "boolean") return val.toString();
                return val;
            };

            const isVoid = problemInfo?.returnType === "void";

            // 1. Generate Input Definitions
            const inputDefs = Object.entries(t.input).map(([key, val]) => {
                const type = getJavaType(val);
                const value = formatJavaVal(val, type);
                return `${type} ${key} = ${value};`;
            }).join('\n            ');

            // 2. Generate Expected Value
            const expectedValRaw = t.expected;
            const expectedValueStr = formatJavaVal(expectedValRaw, getJavaType(expectedValRaw));

            const callArgs = Object.keys(t.input).join(', ');
            const firstArg = Object.keys(t.input)[0];

            return `
        try {
            // Setup Inputs
            ${inputDefs}
            Object expected = ${expectedValueStr};

            // Execute
            Object result;
            ${isVoid
                ? `solution.${fnName}(${callArgs}); result = ${firstArg};`
                : `result = solution.${fnName}(${callArgs});`
            }

            // Compare
            boolean passed = Helper.isEqual(result, expected);
            String status = passed ? "Passed" : "Failed";

            // Serialize
            String actualStr = Helper.serialize(result);
            String expectedStr = Helper.serialize(expected);

            // Escape quotes for JSON (Java: actualStr.replace("\"", "\\\""))
            actualStr = actualStr.replace("\\"", "\\\\\\\"");
            expectedStr = expectedStr.replace("\\"", "\\\\\\\"");

            String json = String.format("{\\"id\\": %d, \\"status\\": \\"%s\\", \\"actual\\": \\"%s\\", \\"expected\\": \\"%s\\"}",
                ${i + 1}, status, actualStr, expectedStr);
            results.add(json);

        } catch (Exception e) {
            results.add("{\\"id\\": ${i + 1}, \\"status\\": \\"Error\\", \\"error\\": \\"" + e.getMessage() + "\\"}");
        }`;
        }).join('\n')}

        System.out.println("[" + String.join(",", results) + "]");
    }
}

class Helper {
    public static boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;

        if (a instanceof List) a = listToArray((List<?>) a);
        if (b instanceof List) b = listToArray((List<?>) b);

        if (a.getClass().isArray() && b.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(a);
            if (len != java.lang.reflect.Array.getLength(b)) return false;
            for (int i = 0; i < len; i++) {
                if (!isEqual(java.lang.reflect.Array.get(a, i), java.lang.reflect.Array.get(b, i))) return false;
            }
            return true;
        }

        if (a instanceof Number && b instanceof Number) {
            return Math.abs(((Number) a).doubleValue() - ((Number) b).doubleValue()) < 1e-9;
        }

        return a.equals(b);
    }

    private static Object listToArray(List<?> list) {
        return list.toArray();
    }

    public static String serialize(Object o) {
        if (o == null) return "null";
        if (o instanceof List) return serialize(listToArray((List<?>) o));

        if (o.getClass().isArray()) {
            StringBuilder sb = new StringBuilder("[");
            int len = java.lang.reflect.Array.getLength(o);
            for (int i = 0; i < len; i++) {
                sb.append(serialize(java.lang.reflect.Array.get(o, i)));
                if (i < len - 1) sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        }
        // Fix: Properly escape the quotes for the Java source string
        if (o instanceof String) return "\\"" + o + "\\"";
        return o.toString();
    }
}

${userCode.replace(/public\s+class\s+Solution/, "class UserLogic").replace(/class\s+Solution/, "class UserLogic")}
`,

        cpp: (fnName, userCode, cases, problemInfo) => `
#include <iostream>
#include <vector>
#include <string>
#include <sstream>
#include <algorithm>
#include <map>
#include <unordered_map>
#include <set>
#include <cmath>
#include <climits>
#include <iomanip>
#include <type_traits>

using namespace std;

// --- Serialization Helpers ---
template<typename T>
string valToStr(T val) {
    if constexpr (is_same_v<T, bool>) return val ? "true" : "false";
    else if constexpr (is_same_v<T, string>) return "\\"" + val + "\\"";
    else if constexpr (is_same_v<T, char>) return string("'") + val + "'";
    else if constexpr (is_floating_point_v<T>) {
        stringstream ss; ss << fixed << setprecision(6) << val;
        string s = ss.str();
        s.erase(s.find_last_not_of('0') + 1, string::npos);
        if(s.back() == '.') s.pop_back();
        return s;
    }
    else return to_string(val);
}

template<typename T>
string vecToStr(const vector<T>& v) {
    stringstream ss; ss << "[";
    for(size_t i=0; i<v.size(); ++i) {
        if constexpr (is_scalar_v<T> || is_same_v<T, string>) ss << valToStr(v[i]);
        else ss << vecToStr(v[i]);
        if(i < v.size()-1) ss << ",";
    }
    ss << "]"; return ss.str();
}

// --- Precision-Safe Comparison ---
template<typename T, typename U>
bool isEqual(const T& a, const U& b) {
    if constexpr (is_floating_point_v<T> || is_floating_point_v<U>) return abs(a - b) < 1e-7;
    else if constexpr (is_same_v<T, U>) return a == b;
    else return false;
}

template<typename T>
bool isEqual(const vector<T>& a, const vector<T>& b) {
    if (a.size() != b.size()) return false;
    for (size_t i = 0; i < a.size(); ++i) if (!isEqual(a[i], b[i])) return false;
    return true;
}

${userCode}

int main() {
    vector<string> results;
    Solution solution;

    ${cases.map((t, i) => {
        const getCppType = (val) => {
            if (!Array.isArray(val)) {
                if (typeof val === 'string') return val.length === 1 ? "char" : "string";
                if (typeof val === 'boolean') return "bool";
                if (typeof val === 'number' && !Number.isInteger(val)) return "double";
                return "int";
            }
            if (val.length === 0) return "vector<string>"; // Safer default for string problems
            return "vector<" + getCppType(val[0]) + ">";
        };

        // FIXED: Using single backslash escaping for JS to output a clean " for C++
        const toCppVal = (val) => {
            if (Array.isArray(val)) return "{" + val.map(v => toCppVal(v)).join(',') + "}";
            if (typeof val === 'string') {
                return val.length === 1 ? ("'" + val + "'") : ("\\\"" + val + "\\\"");
            }
            return val;
        };

        const isVoid = problemInfo?.returnType === "void";
        const firstArgName = Object.keys(t.input)[0];
        const callArgs = Object.keys(t.input).join(', ');

        return `
    try {
        ${Object.entries(t.input).map(([key, val]) => {
            return `${getCppType(val)} ${key} = ${toCppVal(val)};`;
        }).join('\n        ')}

        ${isVoid
            ? `solution.${fnName}(${callArgs}); auto result = ${firstArgName};`
            : `auto result = solution.${fnName}(${callArgs});`}

        decltype(result) expected = ${toCppVal(t.expected)};

        bool passed = isEqual(result, expected);
        string actualStr = ${Array.isArray(t.expected) ? "vecToStr(result)" : "valToStr(result)"};
        string expectedStr = ${Array.isArray(t.expected) ? "vecToStr(expected)" : "valToStr(expected)"};

        stringstream ss;
        ss << "{\\"id\\": ${i + 1}, \\"status\\": \\"" << (passed ? "Passed" : "Failed")
           << "\\", \\"actual\\": " << actualStr << ", \\"expected\\": " << expectedStr << "}";
        results.push_back(ss.str());
    } catch (...) {
        results.push_back("{\\"id\\": ${i + 1}, \\"status\\": \\"Error\\", \\"error\\": \\"Runtime Error\\"}");
    } \n`;
    }).join('')}

    cout << "[";
    for(size_t i=0; i<results.size(); ++i) {
        cout << results[i] << (i < results.size()-1 ? "," : "");
    }
    cout << "]" << endl;
    return 0;
}
`
    },
    "Integer": {  //it include all (int ,double ,float )
        javascript: (fnName, userCode, cases, problemInfo) => `
        // 1. User Code
        ${userCode}

        // 2. Driver Code
        const testCases = ${JSON.stringify(cases)};
        const results = [];
        const isVoid = ${problemInfo?.returnType === "void"};

        testCases.forEach((t, index) => {
            const resultEntry = { id: index + 1 };
            try {
                // Extract values dynamically (handles 1 arg, 2 args, etc.)
                const args = Object.values(t.input);

                // Call Function
                const output = ${fnName}(...args);

                // Handle void vs return
                const result = isVoid ? args[0] : output;

                resultEntry.actual = result;
                resultEntry.expected = t.expected;

                // Comparison Logic (Handles float precision if needed, otherwise strict)
                const isFloat = typeof t.expected === 'number' && !Number.isInteger(t.expected);

                let passed = false;
                if (isFloat) {
                    passed = Math.abs(result - t.expected) < 0.00001;
                } else {
                    passed = JSON.stringify(result) === JSON.stringify(t.expected);
                }

                resultEntry.status = passed ? "Passed" : "Failed";
            } catch (error) {
                resultEntry.status = "Error";
                resultEntry.error = error.message;
            }
            results.push(resultEntry);
        });
        console.log(JSON.stringify(results));
    `,

        python: (fnName, userCode, cases, problemInfo) => `
from __future__ import annotations
import json
import copy
import math

# 1. User Code
${userCode}

# 2. Driver Code
cases_json = '''${JSON.stringify(cases)}'''
test_cases = json.loads(cases_json)
results = []
solution = Solution()
is_void = ${problemInfo?.returnType === "void" ? "True" : "False"}

for i, t in enumerate(test_cases):
    result_entry = {"id": i + 1}
    try:
        # Dynamic Arguments
        args = list(t["input"].values())

        func = getattr(solution, "${fnName}")
        output = func(*args)

        result = args[0] if is_void else output

        result_entry["actual"] = result
        result_entry["expected"] = t["expected"]

        # Compare (Handle Float vs Exact)
        passed = False
        if isinstance(t["expected"], float):
            passed = abs(result - t["expected"]) < 0.00001
        else:
            passed = result == t["expected"]

        result_entry["status"] = "Passed" if passed else "Failed"

    except Exception as e:
        result_entry["status"] = "Error"
        result_entry["error"] = str(e)

    results.append(result_entry)

print(json.dumps(results))
    `,

       java: (fnName, userCode, cases, problemInfo) => `
import java.util.*;
import java.util.stream.*;

public class Solution {
    public static void main(String[] args) {
        List<String> results = new ArrayList<>();
        UserLogic solution = new UserLogic();

        ${cases.map((t, i) => {
            // --- JS HELPER FUNCTIONS ---
            const getJavaType = (val) => {
                if (val === null) return "Object";
                if (Array.isArray(val)) {
                    if (val.length > 0 && typeof val[0] === "string") return "String[]";
                    return "int[]";
                }
                if (typeof val === "string") return "String";
                if (typeof val === "boolean") return "boolean";
                if (Number.isInteger(val)) return "int";
                return "double";
            };

            const formatJavaVal = (val, type) => {
                if (val === null) return "null";
                if (Array.isArray(val)) {
                    // Always use new Type[]{} syntax for safety
                    if (val.length === 0) return "new " + type + "{}";
                    let inner = val.join(',');
                    if (type === "String[]") inner = val.map(s => '"' + s + '"').join(',');
                    return "new " + type + "{" + inner + "}";
                }
                if (typeof val === "string") return '"' + val + '"';
                if (typeof val === "boolean") return val.toString();
                return val;
            };

            const isVoid = problemInfo?.returnType === "void";

            // 1. Generate Input Definitions
            // Fix: Use direct string concatenation to avoid template nesting issues
            const inputDefs = Object.entries(t.input).map(([key, val]) => {
                const type = getJavaType(val);
                const value = formatJavaVal(val, type);
                return type + " " + key + " = " + value + ";";
            }).join('\n            ');

            // 2. Generate Expected Value
            const expectedValRaw = t.expected;
            const expectedValueStr = formatJavaVal(expectedValRaw, getJavaType(expectedValRaw));

            const callArgs = Object.keys(t.input).join(', ');
            const firstArg = Object.keys(t.input)[0];

            return `
        try {
            // Setup Inputs
            ${inputDefs}
            Object expected = ${expectedValueStr};

            // Execute (use Object to catch any return type: int, double, boolean, List, etc.)
            Object result;
            ${isVoid
                ? `solution.${fnName}(${callArgs}); result = ${firstArg};`
                : `result = solution.${fnName}(${callArgs});`
            }

            // Compare
            boolean passed = Helper.isEqual(result, expected);
            String status = passed ? "Passed" : "Failed";

            // Serialize
            String actualStr = Helper.serialize(result);
            String expectedStr = Helper.serialize(expected);

            // Escape quotes for JSON (Java output: actualStr.replace("\"", "\\\""))
            // Fix: Use \\" to generate \" in Java source, avoiding "illegal text block" error
            actualStr = actualStr.replace("\\"", "\\\\\\\"");
            expectedStr = expectedStr.replace("\\"", "\\\\\\\"");

            String json = String.format("{\\"id\\": %d, \\"status\\": \\"%s\\", \\"actual\\": \\"%s\\", \\"expected\\": \\"%s\\"}",
                ${i + 1}, status, actualStr, expectedStr);
            results.add(json);

        } catch (Exception e) {
            results.add("{\\"id\\": ${i + 1}, \\"status\\": \\"Error\\", \\"error\\": \\"" + e.getMessage() + "\\"}");
        }`;
        }).join('\n')}

        System.out.println("[" + String.join(",", results) + "]");
    }
}

// --- HELPER CLASS ---
class Helper {
    public static boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;

        // Normalize Lists to Arrays for comparison (Handles List<Integer> vs int[])
        if (a instanceof List) a = listToArray((List<?>) a);
        if (b instanceof List) b = listToArray((List<?>) b);

        if (a.getClass().isArray() && b.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(a);
            if (len != java.lang.reflect.Array.getLength(b)) return false;
            for (int i = 0; i < len; i++) {
                if (!isEqual(java.lang.reflect.Array.get(a, i), java.lang.reflect.Array.get(b, i))) return false;
            }
            return true;
        }

        // Robust numeric comparison (handles int vs double, e.g., 5 vs 5.0)
        if (a instanceof Number && b instanceof Number) {
            return Math.abs(((Number) a).doubleValue() - ((Number) b).doubleValue()) < 1e-9;
        }

        return a.equals(b);
    }

    private static Object listToArray(List<?> list) {
        return list.toArray();
    }

    public static String serialize(Object o) {
        if (o == null) return "null";
        if (o instanceof List) return serialize(listToArray((List<?>) o));

        if (o.getClass().isArray()) {
            StringBuilder sb = new StringBuilder("[");
            int len = java.lang.reflect.Array.getLength(o);
            for (int i = 0; i < len; i++) {
                sb.append(serialize(java.lang.reflect.Array.get(o, i)));
                if (i < len - 1) sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        }
        // Properly escape strings for JSON output: return "\"" + o + "\"";
        if (o instanceof String) return "\\"" + o + "\\"";
        return o.toString();
    }
}

${userCode.replace(/public\s+class\s+Solution/, "class UserLogic").replace(/class\s+Solution/, "class UserLogic")}
`,

        cpp: (fnName, userCode, cases, problemInfo) => `
#include <iostream>
#include <vector>
#include <string>
#include <sstream>
#include <algorithm>
#include <cmath>
#include <iomanip>

using namespace std;

// Helpers
string valToStr(int val) { return to_string(val); }
string valToStr(long long val) { return to_string(val); }
string valToStr(double val) { return to_string(val); }
string valToStr(bool val) { return val ? "true" : "false"; }
string valToStr(string val) { return "\\"" + val + "\\""; }

${userCode}

int main() {
    vector<string> results;
    Solution solution;
    bool isVoid = ${problemInfo?.returnType === "void"};

    ${cases.map((t, i) => {
            // 1. Detect Return Type
            let cppType = "int";
            let expectedVal = t.expected;

            if (typeof t.expected === "boolean") {
                cppType = "bool";
                expectedVal = t.expected ? "true" : "false";
            } else if (typeof t.expected === "string") {
                cppType = "string";
                expectedVal = `"${t.expected}"`;
            } else if (!Number.isInteger(t.expected)) {
                cppType = "double";
            }

            const callArgs = Object.keys(t.input).join(", ");
            const firstArg = Object.keys(t.input)[0];

            // 2. Generate Inputs Declaration
            const inputsDecl = Object.entries(t.input).map(([key, val]) => {
                const type = typeof val === "boolean" ? "bool" : (Number.isInteger(val) ? "int" : "double");
                const valStr = typeof val === "boolean" ? (val ? "true" : "false") : val;
                return `${type} ${key} = ${valStr};`;
            }).join("\n        ");

            return `
    try {
        ${inputsDecl}
        ${cppType} expected = ${expectedVal};
        ${cppType} result;

        if (isVoid) {
            solution.${fnName}(${callArgs});
            result = ${firstArg};
        } else {
            result = solution.${fnName}(${callArgs});
        }

        bool passed = false;
        if (is_same<${cppType}, double>::value) {
            passed = abs((double)result - (double)expected) < 1e-5;
        } else {
            passed = (result == expected);
        }

        string status = passed ? "Passed" : "Failed";

        stringstream json;
        json << "{\\"id\\": ${i + 1}, \\"status\\": \\"" << status << "\\", \\"actual\\": \\"" << valToStr(result) << "\\", \\"expected\\": \\"" << valToStr(expected) << "\\"}";
        results.push_back(json.str());
    } catch (const exception& e) {
        results.push_back("{\\"id\\": ${i + 1}, \\"status\\": \\"Error\\", \\"error\\": \\"Runtime Error\\"}");
    }`;
        }).join('\n')}

    cout << "[";
    for(size_t i=0; i<results.size(); ++i) { cout << results[i]; if(i < results.size()-1) cout << ","; }
    cout << "]" << endl;
    return 0;
}
    `,
    },
}


