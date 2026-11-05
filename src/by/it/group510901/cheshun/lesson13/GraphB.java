package by.it.group510901.cheshun.lesson13;

import java.util.*;

public class GraphB {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine().trim();

        Map<Integer, List<Integer>> graph = new HashMap<>();
        Set<Integer> vertices = new HashSet<>();

        if (!line.isEmpty()) {
            String[] edges = line.split(",");
            for (String edge : edges) {
                String[] parts = edge.split("->");
                int from = Integer.parseInt(parts[0].trim());
                int to = Integer.parseInt(parts[1].trim());

                graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
                vertices.add(from);
                vertices.add(to);
            }
        }

        Set<Integer> visited = new HashSet<>();
        Set<Integer> inStack = new HashSet<>();
        boolean hasCycle = false;

        for (int v : vertices) {
            if (dfs(v, graph, visited, inStack)) {
                hasCycle = true;
                break;
            }
        }

        System.out.println(hasCycle ? "yes" : "no");
    }

    private static boolean dfs(int v, Map<Integer, List<Integer>> graph,
                               Set<Integer> visited, Set<Integer> inStack) {
        if (inStack.contains(v)) return true;
        if (visited.contains(v)) return false;

        visited.add(v);
        inStack.add(v);

        for (int neighbor : graph.getOrDefault(v, Collections.emptyList())) {
            if (dfs(neighbor, graph, visited, inStack)) return true;
        }

        inStack.remove(v);
        return false;
    }
}