import java.io.*;
import java.util.*;

public class Main {
	static int villageSize, warriorNumber; // 마을의 크기, 전사의 수
	static boolean[][] canSee;
	static int[][] village; // 마을의 정보. 도로는 0, 도로가 아닌 곳은 1
	static Point start, end, now; // 시작 좌표, 종료 좌표, 현재 좌표
	static Point[] warriors; // 전사들의 위치 저장
	static ArrayDeque<Point> way = new ArrayDeque<>(); // 공원까지 가는 길

	public static void main(String[] args) throws IOException {
		StringBuilder sb = new StringBuilder();

		initialize(); // 초기화

		if (findWayToPark() == false) { // 공원까지 가는 길이 없다면 -1 출력 후 종료
			System.out.println(-1);

			return;
		}

		while (!way.isEmpty()) {
			medusaMove(); // 메두사의 이동

			int becomeStone = medusaSee(); // 메두사의 시선

			int move = warriorMove(); // 전사들의 이동

			int attack = warriorAttack(); // 전사들의 공격

			sb.append(move).append(" ").append(becomeStone).append(" ").append(attack).append("\n");
		}
		sb.append(0);

		System.out.println(sb);
	}

	static int warriorAttack() { // 전사들의 공격
		int attack = 0; // 메두사를 공격한 전사의 수 반환

		for (int i = 0; i < warriorNumber; i++) {
			Point w = warriors[i];

			if (w.r == now.r && w.c == now.c) {
				attack += 1;

				warriors[i] = warriors[--warriorNumber];
				i -= 1;
			}
		}

		return attack;
	}

	static int warriorMove() { // 전사들의 이동
		// 돌이 된 전사는 움직일 수 없으며 턴이 종료되었을 때 돌에서 풀려남
		// 메두사의 시야에 들어오는 곳으로는 이동 불가
		int move = 0; // 모든 전사가 이동한 거리의 합 반환

		int[] dr = { -1, 1, 0, 0 };
		int[] dc = { 0, 0, -1, 1 };
		for (int i = 0; i < warriorNumber; i++) { // 첫 번째 이동
			// 메두사와 거리를 줄일 수 있는 방향으로 한 칸 이동
			// 상하좌우 순
			Point w = warriors[i];

			if (canSee[w.r][w.c]) { // 돌이 되었다면 움직이지 않음
				continue;
			}

			int distance = Math.abs(w.r - now.r) + Math.abs(w.c - now.c);

			for (int d = 0; d < 4; d++) {
				int r = w.r + dr[d];
				int c = w.c + dc[d];

				if (Math.abs(r - now.r) + Math.abs(c - now.c) >= distance) { // 거리를 줄일 수 있는 방향으로 이동
					continue;
				}

				if (0 <= r && r < villageSize && 0 <= c && c < villageSize) {
					if (canSee[r][c] == false) { // 메두사의 시야가 닿지 않는 곳이라면
						move += 1;

						w.r = r; // 이동
						w.c = c;

						break;
					}
				}
			}
		}

		dr = new int[] { 0, 0, -1, 1 };
		dc = new int[] { -1, 1, 0, 0 };
		for (int i = 0; i < warriorNumber; i++) { // 두 번째 이동
			// 메두사와 거리를 줄일 수 있는 방향으로 한 칸 이동
			// 좌우상하 순

			Point w = warriors[i];

			if (canSee[w.r][w.c]) { // 돌이 되었다면 움직이지 않음
				continue;
			}

			int distance = Math.abs(w.r - now.r) + Math.abs(w.c - now.c);

			for (int d = 0; d < 4; d++) {
				int r = w.r + dr[d];
				int c = w.c + dc[d];

				if (Math.abs(r - now.r) + Math.abs(c - now.c) >= distance) { // 거리를 줄일 수 있는 방향으로 이동
					continue;
				}

				if (0 <= r && r < villageSize && 0 <= c && c < villageSize) {
					if (canSee[r][c] == false) { // 메두사의 시야가 닿지 않는 곳이라면
						move += 1;

						w.r = r; // 이동
						w.c = c;

						break;
					}
				}
			}
		}

		return move;
	}

	static int medusaSee() { // 메두사의 시선
		// 최대한 많은 수의 전사들을 볼 수 있는 방향을 봄
		// 같은 수의 전사를 볼 수 있는 방향이 여러 개라면 상하좌우 순
		// 돌이 된 전사는 움직일 수 없으며 턴이 종료되었을 때 돌에서 풀려남

		int up = medusaSeeUp();
		int down = medusaSeeDown();
		int left = medusaSeeLeft();
		int right = medusaSeeRight();
		// 돌이 된 전사의 수 반환

		int max = Math.max(up, Math.max(down, Math.max(left, right))); // 상하좌우 중 가장 많이 보는 방향 확인

		// 가장 많이 보는 곳을 바라보기
		if (max == up) {
			medusaSeeUp();
		}

		else if (max == down) {
			medusaSeeDown();
		}

		else if (max == left) {
			medusaSeeLeft();
		}

		else {
			medusaSeeRight();
		}

		return max;
	}

	private static int medusaSeeRight() { // 오른쪽 방향 보기
		ArrayDeque<Point> bfs = new ArrayDeque<>();
		canSee = new boolean[villageSize][villageSize];

		bfs.add(new Point(now.r, now.c));

		int[] dr = { -1, 0, 1 };

		while (!bfs.isEmpty()) { // 볼 수 있는 곳을 true 표시
			Point p = bfs.remove();

			for (int d = 0; d < 3; d++) {
				if (0 <= p.r + dr[d] && p.r + dr[d] < villageSize && p.c + 1 < villageSize) {
					if (canSee[p.r + dr[d]][p.c + 1] == false) {
						canSee[p.r + dr[d]][p.c + 1] = true;
						bfs.add(new Point(p.r + dr[d], p.c + 1));
					}
				}
			}
		}

		for (int i = 0; i < warriorNumber; i++) { // 병사를 배치하고, 가려지는 위치 표시
			Point w = warriors[i];

			if (canSee[w.r][w.c]) { // 메두사가 보는 곳이라면
				if (w.r == now.r) { // 메두사의 정면
					int c = w.c;

					while (true) {
						c += 1;

						if (c < villageSize) {
							canSee[w.r][c] = false;
						}

						else {
							break;
						}
					}
				}

				else if (w.r < now.r) { // 메두사의 위쪽에 위치
					bfs = new ArrayDeque<>();
					bfs.add(new Point(w.r, w.c));

					dr = new int[] { -1, 0 };

					while (!bfs.isEmpty()) {
						Point p = bfs.remove();

						for (int d = 0; d < 2; d++) {
							if (0 <= p.r + dr[d] && p.r + dr[d] < villageSize && p.c + 1 < villageSize) {
								if (canSee[p.r + dr[d]][p.c + 1]) {
									bfs.add(new Point(p.r + dr[d], p.c + 1));
									canSee[p.r + dr[d]][p.c + 1] = false;
								}
							}
						}
					}
				}

				else { // 메두사의 아래쪽에 위치
					bfs = new ArrayDeque<>();
					bfs.add(new Point(w.r, w.c));

					dr = new int[] { 0, 1 };

					while (!bfs.isEmpty()) {
						Point p = bfs.remove();

						for (int d = 0; d < 2; d++) {
							if (0 <= p.r + dr[d] && p.r + dr[d] < villageSize && p.c + 1 < villageSize) {
								if (canSee[p.r + dr[d]][p.c + 1]) {
									bfs.add(new Point(p.r + dr[d], p.c + 1));
									canSee[p.r + dr[d]][p.c + 1] = false;
								}
							}
						}
					}
				}
			}
		}

		int count = 0;
		for (int i = 0; i < warriorNumber; i++) { // 돌이 되는 병사의 수 카운트
			Point w = warriors[i];

			if (canSee[w.r][w.c]) {
				count += 1;
			}
		}

		return count;
	}

	private static int medusaSeeLeft() { // 왼쪽 방향 보기
		ArrayDeque<Point> bfs = new ArrayDeque<>();
		canSee = new boolean[villageSize][villageSize];

		bfs.add(new Point(now.r, now.c));

		int[] dr = { -1, 0, 1 };

		while (!bfs.isEmpty()) { // 볼 수 있는 곳을 true 표시
			Point p = bfs.remove();

			for (int d = 0; d < 3; d++) {
				if (0 <= p.r + dr[d] && p.r + dr[d] < villageSize && 0 <= p.c - 1) {
					if (canSee[p.r + dr[d]][p.c - 1] == false) {
						canSee[p.r + dr[d]][p.c - 1] = true;
						bfs.add(new Point(p.r + dr[d], p.c - 1));
					}
				}
			}
		}

		for (int i = 0; i < warriorNumber; i++) { // 병사를 배치하고, 가려지는 위치 표시
			Point w = warriors[i];

			if (canSee[w.r][w.c]) { // 메두사가 보는 곳이라면
				if (w.r == now.r) { // 메두사의 정면
					int c = w.c;

					while (true) {
						c -= 1;

						if (0 <= c) {
							canSee[w.r][c] = false;
						}

						else {
							break;
						}
					}
				}

				else if (w.r < now.r) { // 메두사의 위쪽에 위치
					bfs = new ArrayDeque<>();
					bfs.add(new Point(w.r, w.c));

					dr = new int[] { -1, 0 };

					while (!bfs.isEmpty()) {
						Point p = bfs.remove();

						for (int d = 0; d < 2; d++) {
							if (0 <= p.r + dr[d] && p.r + dr[d] < villageSize && 0 <= p.c - 1) {
								if (canSee[p.r + dr[d]][p.c - 1]) {
									bfs.add(new Point(p.r + dr[d], p.c - 1));
									canSee[p.r + dr[d]][p.c - 1] = false;
								}
							}
						}
					}
				}

				else { // 메두사의 아래쪽에 위치
					bfs = new ArrayDeque<>();
					bfs.add(new Point(w.r, w.c));

					dr = new int[] { 0, 1 };

					while (!bfs.isEmpty()) {
						Point p = bfs.remove();

						for (int d = 0; d < 2; d++) {
							if (0 <= p.r + dr[d] && p.r + dr[d] < villageSize && 0 <= p.c - 1) {
								if (canSee[p.r + dr[d]][p.c - 1]) {
									bfs.add(new Point(p.r + dr[d], p.c - 1));
									canSee[p.r + dr[d]][p.c - 1] = false;
								}
							}
						}
					}
				}
			}
		}

		int count = 0;
		for (int i = 0; i < warriorNumber; i++) { // 돌이 되는 병사의 수 카운트
			Point w = warriors[i];

			if (canSee[w.r][w.c]) {
				count += 1;
			}
		}

		return count;
	}

	private static int medusaSeeDown() { // 아래 방향 바라보기
		ArrayDeque<Point> bfs = new ArrayDeque<>();
		canSee = new boolean[villageSize][villageSize];

		bfs.add(new Point(now.r, now.c));

		int[] dc = { -1, 0, 1 };

		while (!bfs.isEmpty()) { // 볼 수 있는 곳을 true 표시
			Point p = bfs.remove();

			for (int d = 0; d < 3; d++) {
				if (p.r + 1 < villageSize && 0 <= p.c + dc[d] && p.c + dc[d] < villageSize) {
					if (canSee[p.r + 1][p.c + dc[d]] == false) {
						canSee[p.r + 1][p.c + dc[d]] = true;
						bfs.add(new Point(p.r + 1, p.c + dc[d]));
					}
				}
			}
		}

		for (int i = 0; i < warriorNumber; i++) { // 병사를 배치하고, 가려지는 위치 표시
			Point w = warriors[i];

			if (canSee[w.r][w.c]) { // 메두사가 보는 곳이라면
				if (w.c == now.c) { // 메두사의 정면
					int r = w.r;

					while (true) {
						r += 1;

						if (r < villageSize) {
							canSee[r][w.c] = false;
						}

						else {
							break;
						}
					}
				}

				else if (w.c < now.c) { // 메두사의 왼쪽에 위치
					bfs = new ArrayDeque<>();
					bfs.add(new Point(w.r, w.c));

					dc = new int[] { -1, 0 };

					while (!bfs.isEmpty()) {
						Point p = bfs.remove();

						for (int d = 0; d < 2; d++) {
							if (p.r + 1 < villageSize && 0 <= p.c + dc[d] && p.c + dc[d] < villageSize) {
								if (canSee[p.r + 1][p.c + dc[d]]) {
									bfs.add(new Point(p.r + 1, p.c + dc[d]));
									canSee[p.r + 1][p.c + dc[d]] = false;
								}
							}
						}
					}
				}

				else { // 메두사의 오른쪽에 위치
					bfs = new ArrayDeque<>();
					bfs.add(new Point(w.r, w.c));

					dc = new int[] { 0, 1 };

					while (!bfs.isEmpty()) {
						Point p = bfs.remove();

						for (int d = 0; d < 2; d++) {
							if (p.r + 1 < villageSize && 0 <= p.c + dc[d] && p.c + dc[d] < villageSize) {
								if (canSee[p.r + 1][p.c + dc[d]]) {
									bfs.add(new Point(p.r + 1, p.c + dc[d]));
									canSee[p.r + 1][p.c + dc[d]] = false;
								}
							}
						}
					}
				}
			}
		}

		int count = 0;
		for (int i = 0; i < warriorNumber; i++) { // 돌이 되는 병사의 수 카운트
			Point w = warriors[i];

			if (canSee[w.r][w.c]) {
				count += 1;
			}
		}

		return count;
	}

	static int medusaSeeUp() { // 위쪽 방향 바라보기
		ArrayDeque<Point> bfs = new ArrayDeque<>();
		canSee = new boolean[villageSize][villageSize];

		bfs.add(new Point(now.r, now.c));

		int[] dc = { -1, 0, 1 };

		while (!bfs.isEmpty()) { // 볼 수 있는 곳을 true 표시
			Point p = bfs.remove();

			for (int d = 0; d < 3; d++) {
				if (0 <= p.r - 1 && 0 <= p.c + dc[d] && p.c + dc[d] < villageSize) {
					if (canSee[p.r - 1][p.c + dc[d]] == false) {
						canSee[p.r - 1][p.c + dc[d]] = true;
						bfs.add(new Point(p.r - 1, p.c + dc[d]));
					}
				}
			}
		}

		for (int i = 0; i < warriorNumber; i++) { // 병사를 배치하고, 가려지는 위치 표시
			Point w = warriors[i];

			if (canSee[w.r][w.c]) { // 메두사가 보는 곳이라면
				if (w.c == now.c) { // 메두사의 정면
					int r = w.r;

					while (true) {
						r -= 1;

						if (0 <= r) {
							canSee[r][w.c] = false;
						}

						else {
							break;
						}
					}
				}

				else if (w.c < now.c) { // 메두사의 왼쪽에 위치
					bfs = new ArrayDeque<>();
					bfs.add(new Point(w.r, w.c));

					dc = new int[] { -1, 0 };

					while (!bfs.isEmpty()) {
						Point p = bfs.remove();

						for (int d = 0; d < 2; d++) {
							if (0 <= p.r - 1 && 0 <= p.c + dc[d] && p.c + dc[d] < villageSize) {
								if (canSee[p.r - 1][p.c + dc[d]]) {
									bfs.add(new Point(p.r - 1, p.c + dc[d]));
									canSee[p.r - 1][p.c + dc[d]] = false;
								}
							}
						}
					}
				}

				else { // 메두사의 오른쪽에 위치
					bfs = new ArrayDeque<>();
					bfs.add(new Point(w.r, w.c));

					dc = new int[] { 0, 1 };

					while (!bfs.isEmpty()) {
						Point p = bfs.remove();

						for (int d = 0; d < 2; d++) {
							if (0 <= p.r - 1 && 0 <= p.c + dc[d] && p.c + dc[d] < villageSize) {
								if (canSee[p.r - 1][p.c + dc[d]]) {
									bfs.add(new Point(p.r - 1, p.c + dc[d]));
									canSee[p.r - 1][p.c + dc[d]] = false;
								}
							}
						}
					}
				}
			}
		}

		int count = 0;
		for (int i = 0; i < warriorNumber; i++) { // 돌이 되는 병사의 수 카운트
			Point w = warriors[i];

			if (canSee[w.r][w.c]) {
				count += 1;
			}
		}

		return count;
	}

	static void medusaMove() { // 메두사의 이동
		Point p = way.removeFirst();

		now = new Point(p.r, p.c);

		for (int i = 0; i < warriorNumber; i++) { // 만약 메두사가 이동한 자리에 병사가 있었다면
			Point w = warriors[i];

			if (w.r == now.r && w.c == now.c) {
				warriors[i] = warriors[--warriorNumber];
				i -= 1;
			}
		}
	}

	static boolean findWayToPark() { // 메두사의 최단 경로 구하기
		ArrayDeque<Point> bfs = new ArrayDeque<>();
		Point[][] isVisit = new Point[villageSize][villageSize]; // 이전 좌표를 저장

		int[] dr = { -1, 1, 0, 0 }; // 상하좌우 순
		int[] dc = { 0, 0, -1, 1 };

		bfs.add(new Point(start.r, start.c));
		isVisit[start.r][start.c] = new Point(start.r, start.c);

		while (!bfs.isEmpty()) {
			Point p = bfs.removeFirst();

			for (int d = 0; d < 4; d++) {
				int rr = p.r + dr[d];
				int cc = p.c + dc[d];

				if (0 <= rr && rr < villageSize && 0 <= cc && cc < villageSize) { // 범위 안에 있고
					if (village[rr][cc] == 0) { // 도로이고
						if (isVisit[rr][cc] == null) { // 방문하지 않았다면
							isVisit[rr][cc] = new Point(p.r, p.c); // 이전 좌표를 저장
							bfs.addLast(new Point(rr, cc));
						}
					}
				}
			}
		}

		if (isVisit[end.r][end.c] == null) { // 공원까지 갈 수 없다면
			return false;
		}

		else {
			int r = end.r;
			int c = end.c;

			while (r != start.r || c != start.c) {
				way.addFirst(new Point(r, c));

				int rr = isVisit[r][c].r;
				int cc = isVisit[r][c].c;

				r = rr;
				c = cc;
			}

			way.removeLast();

			return true;
		}
	}

	static void initialize() throws IOException { // 초기화
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;

		st = new StringTokenizer(br.readLine()); // 마을의 크기, 전사의 수 입력
		villageSize = Integer.parseInt(st.nextToken());
		warriorNumber = Integer.parseInt(st.nextToken());

		st = new StringTokenizer(br.readLine()); // 메두사의 집과 공원의 위치 정보 입력
		start = new Point(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		end = new Point(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));

		st = new StringTokenizer(br.readLine()); // 전사들의 좌표 입력
		warriors = new Point[warriorNumber];
		for (int i = 0; i < warriorNumber; i++) {
			warriors[i] = new Point(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		}

		village = new int[villageSize][villageSize];
		for (int r = 0; r < villageSize; r++) { // 마을의 정보 입력
			st = new StringTokenizer(br.readLine());

			for (int c = 0; c < villageSize; c++) {
				village[r][c] = Integer.parseInt(st.nextToken());
			}
		}
	}

	static class Point {
		int r, c;

		Point(int r, int c) {
			this.r = r;
			this.c = c;
		}
	}
}