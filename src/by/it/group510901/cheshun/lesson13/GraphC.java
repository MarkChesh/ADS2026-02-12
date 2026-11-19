package by.it.group510901.cheshun.lesson13;

import java.util.*;

public class GraphC {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        // Парсинг графа
        Map<Character, List<Character>> graph = new HashMap<>();
        Map<Character, List<Character>> reverseGraph = new HashMap<>();
        Set<Character> allVertices = new TreeSet<>();

        String[] edges = input.split(",\\s*");
        for (String edge : edges) {
            char from = edge.charAt(0);
            char to = edge.charAt(3);

            allVertices.add(from);
            allVertices.add(to);

            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            reverseGraph.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
            graph.computeIfAbsent(to, k -> new ArrayList<>());
            reverseGraph.computeIfAbsent(from, k -> new ArrayList<>());
        }

        // Алгоритм Косараджу для нахождения компонент сильной связности
        // Шаг 1: DFS на исходном графе для получения порядка завершения
        Set<Character> visited = new HashSet<>();
        Stack<Character> stack = new Stack<>();

        for (char v : allVertices) {
            if (!visited.contains(v)) {
                dfs1(v, graph, visited, stack);
            }
        }

        // Шаг 2: DFS на обратном графе в порядке стека
        visited.clear();
        List<List<Character>> components = new ArrayList<>();

        while (!stack.isEmpty()) {
            char v = stack.pop();
            if (!visited.contains(v)) {
                List<Character> component = new ArrayList<>();
                dfs2(v, reverseGraph, visited, component);
                Collections.sort(component);
                components.add(component);
            }
        }


        List<List<Character>> sortedComponents = new ArrayList<>();
        Set<Character> used = new HashSet<>();

        while (sortedComponents.size() < components.size()) {
            for (List<Character> comp : components) {
                if (used.contains(comp.get(0))) continue;

                
                boolean isSource = true;
                for (Character v : comp) {
                    for (Character from : reverseGraph.get(v)) {
                        if (!comp.contains(from) && !used.contains(from)) {
                            isSource = false;
                            break;
                        }
                    }
                    if (!isSource) break;
                }

                if (isSource) {
                    sortedComponents.add(comp);
                    used.addAll(comp);
                    break;
                }
            }
        }

        // Вывод результата
        for (List<Character> comp : sortedComponents) {
            StringBuilder sb = new StringBuilder();
            for (char c : comp) {
                sb.append(c);
            }
            System.out.println(sb.toString());
        }
    }

    private static void dfs1(char v, Map<Character, List<Character>> graph,
                             Set<Character> visited, Stack<Character> stack) {
        visited.add(v);
        for (char neighbor : graph.get(v)) {
            if (!visited.contains(neighbor)) {
                dfs1(neighbor, graph, visited, stack);
            }
        }
        stack.push(v);
    }

    private static void dfs2(char v, Map<Character, List<Character>> reverseGraph,
                             Set<Character> visited, List<Character> component) {
        visited.add(v);
        component.add(v);
        for (char neighbor : reverseGraph.get(v)) {
            if (!visited.contains(neighbor)) {
                dfs2(neighbor, reverseGraph, visited, component);
            }
        }
    }
}