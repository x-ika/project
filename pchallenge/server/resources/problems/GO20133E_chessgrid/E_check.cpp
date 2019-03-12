// Problem E - checker code

#include <cstdio>
#include <cstdlib>
#include <vector>
#include <algorithm>

#pragma warning(disable: 4996)

using namespace std;

FILE *input, *out, *corr;

int open_files(char** argv) {
	try {
		if (!argv[1]) {
			throw "Input file not found!";
		} else {
			input = fopen(argv[1], "r");
			if (!input) {
				throw "Input file not found!";
			}
		}
		if (!argv[2]) {
			throw "Output file not found!";
		} else {
			out = fopen(argv[2], "r");
			if (!out) {
				throw "Output file not found!";;
			}
		}
		if (!argv[3]) {
			throw "Correct output file not found!";
		} else {
			corr = fopen(argv[3], "r");
			if (!corr) {
				throw "Correct output file not found!";;
			}
		}
	} catch (char* err) {
		printf("%s\n", err);
		return 6;
	}
	
	return 0;
}

const int nmax = 101;
int n, m, a[nmax][nmax];

void col(int j) {
    int t = a[n - 1][j];
    for (int i = n; i-- > 1;) {
        a[i][j] = a[i - 1][j];
    }
    a[0][j] = t;
}

void row(int i) {
    int t = a[i][m - 1];
    for (int j = m; j-- > 1;) {
        a[i][j] = a[i][j - 1];
    }
    a[i][0] = t;
}

int get_answer() {
    int* b = new int[n * m];
    for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			b[i * m + j] = -a[i][j];
		}
    }
    sort(b, b + n * m);
    int s = 0;
    for (int i = 0; i < n * m / 2; i++) {
        s += b[i];
    }
    return s;
}

int get_solution_result(vector<string> &output) {
	for (unsigned int i = 0; i < output.size(); i++) {
		string s = output[i];
		for (unsigned int j = 1; j < s.length(); i++) {
			if (s[j] < '0' || s[j] > '9') return -1;
		}
		int ind = atoi(s.substr(1, s.length()).c_str());
		if (s[0] == 'R') {
			if (ind < 1 || ind > n) return -1;
			row(ind - 1);
		} else if (s[0] == 'C') {
			if (ind < 1 || ind > m) return -1;
			col(ind - 1);
		} else {
			return -1;
		}
	}
	int s = 0;
    for (int p = 0; p < n; p++) {
        for (int q = p & 1; q < m; q += 2) {
            s += a[p][q];
        }
    }
	return s;
}

int main(int argc, char** argv) {
	int f = open_files(argv);
	if (f) {
		return f;
	}

	// 1. Read input
	if (fscanf(input, "%d%d", &n, &m) != 2)
	{
		puts("Invalid input file!");
		return 6;
	}
	for(int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			if (fscanf(input, "%d", &a[i][j]) != 1) {
				puts("Invalid input file!");
				return 6;
			}
		}
	}

	// 2. Read output
	int k;
	if (fscanf(out, "%d", &k) != 1)
	{
		puts("Bad output file!");
		return 4;
	}
	vector<string> output(k);
	for (int i = 0; i < k; i++) {
		string s;
		if (fscanf(out, "%s", &s) != 1) {
			puts("Bad output file!");
			return 4;
		}
		output.push_back(s);
	}

	// 3. check correctness
	int ret = get_solution_result(output);
	if (k > 100000 || ret < 0) {
		puts("Bad output format!");
		return 4;
	}
	if (ret != get_answer()) {
		puts("Optimal sum not reached");
		return 5;
	}

	puts("OK");
	return 0;
}
