import java.util.*;
import java.io.*;

public class Main {
	static int maxCoin = 0;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;

		int size = Integer.parseInt(br.readLine());
		int[][] map = new int[size][size];

		for (int r = 0; r < size; r++) {
			st = new StringTokenizer(br.readLine(), " ");

			for (int c = 0; c < size; c++) {
				map[r][c] = Integer.parseInt(st.nextToken());
			}
		}

		int[] dr = { -1, -1, 0, 1, 1, 1, 0, -1 };
		int[] dc = { 0, 1, 1, 1, 0, -1, -1, -1 };
		for (int r = 0; r < size; r++) {
			int sumCoin = 0;

			for (int c = 0; c < size; c++) {
				sumCoin = map[r][c];

				for (int d = 0; d < 8; d++) {
					int rr = r + dr[d];
					int cc = c + dc[d];

					if (0 <= rr && rr < size && 0 <= cc && cc < size) { // 범위 안에 있다면
						sumCoin += map[rr][cc];
					}
				}
                
				maxCoin = Math.max(maxCoin, sumCoin);
			}
		}

		System.out.println(maxCoin);
	}
}