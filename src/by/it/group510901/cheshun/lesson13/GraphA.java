package by.it.group510901.cheshun.lesson13;

import java.util.*;

public class GraphA {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String input = sc.nextLine().trim();
        sc.close();

        if (input.isEmpty()) return;

        Set<String> vertices = new HashSet<>();
        List<String[]> edges = new ArrayList<>();


        String[] parts = input.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            String[] nodes = part.split("->");
            if (nodes.length == 2) {
                String from = nodes[0].trim();
                String to = nodes[1].trim();
                vertices.add(from);
                vertices.add(to);
                edges.add(new String[]{from, to});
            }
        }

        Map<String, List<String>> adj = new HashMap<>();
        Map<String, Integer> indegree = new HashMap<>();

        for (String v : vertices) {
            adj.put(v, new ArrayList<>());
            indegree.put(v, 0);
        }

        for (String[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            indegree.put(edge[1], indegree.get(edge[1]) + 1);
        }

        // Компаратор: если обе вершины - числа, сравниваем их как числа.
        // Иначе сравниваем как строки (лексикографически).
        Comparator<String> comparator = (s1, s2) -> {
            try {
                int n1 = Integer.parseInt(s1);
                int n2 = Integer.parseInt(s2);
                return Integer.compare(n1, n2);
            } catch (NumberFormatException e) {
                return s1.compareTo(s2);
            }
        };

        // Приоритетная очередь с нашим умным компаратором
        PriorityQueue<String> pq = new PriorityQueue<>(comparator);
        for (String v : vertices) {
            if (indegree.get(v) == 0) {
                pq.add(v);
            }
        }

        List<String> result = new ArrayList<>();

        // Алгоритм Кана
        while (!pq.isEmpty()) {
            String v = pq.poll();
            result.add(v);

            for (String next : adj.get(v)) {
                indegree.put(next, indegree.get(next) - 1);
                if (indegree.get(next) == 0) {
                    pq.add(next);
                }
            }
        }

        // Вывод через пробел
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            if (i > 0) sb.append(" ");
            sb.append(result.get(i));
        }
        System.out.println(sb.toString());
    }
}