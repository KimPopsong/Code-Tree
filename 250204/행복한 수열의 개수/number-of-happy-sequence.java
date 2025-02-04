import java.io.*;
import java.util.*;

public class Main {
	static int happySeqNumber = 0;
	static int size, minStreak;
	static int[][] map;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;

		st = new StringTokenizer(br.readLine());

		size = Integer.parseInt(st.nextToken());
		minStreak = Integer.parseInt(st.nextToken());
		map = new int[size][size];

		for (int r = 0; r < size; r++) {
			st = new StringTokenizer(br.readLine());

			for (int c = 0; c < size; c++) {
				map[r][c] = Integer.parseInt(st.nextToken());
			}
		}

		search: for (int r = 0; r < size; r++) { // 가로 기준 찾기
			int streak = 1;
			int nod = -1;

			for (int c = 0; c < size; c++) {
				if (streak >= minStreak) {
					happySeqNumber += 1;

					continue search;
				}

				if (map[r][c] == nod) {
					streak += 1;

				}

				else {
					streak = 1;
					nod = map[r][c];
				}
			}

			if (streak >= minStreak) {
				happySeqNumber += 1;

				continue search;
			}
		}

		search: for (int c = 0; c < size; c++) { // 세로 기준 찾기
			int streak = 1;
			int nod = -1;

			for (int r = 0; r < size; r++) {
				if (streak >= minStreak) {
					happySeqNumber += 1;

					continue search;
				}

				if (map[r][c] == nod) {
					streak += 1;

				}

				else {
					streak = 1;
					nod = map[r][c];
				}
			}

			if (streak >= minStreak) {
				happySeqNumber += 1;

				continue search;
			}
		}

		System.out.println(happySeqNumber);
	}
}