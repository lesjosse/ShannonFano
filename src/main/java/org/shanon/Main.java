package org.shanon;

import javax.swing.*;
import java.util.*;

public class Main {

    static class Symbol implements Comparable<Symbol> {
        char simbolo;
        int frecuencia;
        String codigo = "";

        Symbol(char symbol, int frequency) {
            this.simbolo = symbol;
            this.frecuencia = frequency;
        }

        @Override
        public int compareTo(Symbol o) {
            // Ordenar de mayor a menor frecuencia
            return o.frecuencia - this.frecuencia;
        }
    }

    public static void main(String[] args) {
        // Iniciamos solicitando el mensaje al usuario cn JOptionPane
        String mensaje = JOptionPane.showInputDialog(null, "Ingrese el mensaje fuente:", "Entrada de mensaje", JOptionPane.QUESTION_MESSAGE);

        if (mensaje == null || mensaje.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se ingresó ningún mensaje.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // calculamos las frecuencias
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : mensaje.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        // Crear lista de símbolos
        List<Symbol> simbolos = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            simbolos.add(new Symbol(entry.getKey(), entry.getValue()));
        }

        // Ordenar símbolos por frecuencia descendente
        Collections.sort(simbolos);

        // Aplicar algoritmo Shannon-Fano
        shannonFano(simbolos, 0, simbolos.size() - 1);

        // Construir tabla para mostrar
        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append(String.format("%-8s | %-10s | %-10s\n", "Símbolo", "Frecuencia", "Código"));
        tableBuilder.append("-*--------------------------------*--\n");
        for (Symbol s : simbolos) {
            // Para mostrar espacios en blanco como 'espacio'
            String displaySymbol = (s.simbolo == ' ') ? "' '" : Character.toString(s.simbolo);
                tableBuilder.append(String.format("%-8s | %-10d | %-10s\n", displaySymbol, s.frecuencia, s.codigo));
        }

        // Construir mapa para codificar el mensaje
        Map<Character, String> codiMap = new HashMap<>();
        for (Symbol s : simbolos) {
            codiMap.put(s.simbolo, s.codigo);
        }

        // Codificar el mensaje completo
        StringBuilder codificarMensaje = new StringBuilder();
        for (char c : mensaje.toCharArray()) {
            codificarMensaje.append(codiMap.get(c));
        }

        // Mostrar resultados en un JTextArea dentro de JOptionPane para mejor formato
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setText("Tabla de símbolos con frecuencia y código Shannon-Fano:\n\n" + tableBuilder.toString() +
                "\nMensaje original:\n" + mensaje +
                "\n\nMensaje codificado en bits:\n" + codificarMensaje.toString() +
                "\n\nTotal de bits del mensaje codificado: " + codificarMensaje.length());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(null, scrollPane, "Resultado Codificación Shannon-Fano", JOptionPane.INFORMATION_MESSAGE);
    }

    // Función recursiva para asignar códigos
    private static void shannonFano(List<Symbol> simbolos, int inicio, int fin) {
        if (inicio >= fin) {
            return; // Un solo símbolo, código asignado
        }

        // Encontrar punto de división para balancear frecuencias
        int total = 0;
        for (int i = inicio; i <= fin; i++) {
            total += simbolos.get(i).frecuencia;
        }

        int half = total / 2;
        int sum = 0;
        int split = inicio;
        for (int i = inicio; i <= fin; i++) {
            sum += simbolos.get(i).frecuencia;
            if (sum >= half) {
                split = i;
                break;
            }
        }

        // asignamos cero a la mitad superior
        for (int i = inicio; i <= split; i++) {
            simbolos.get(i).codigo += "0";
        }
        // asignamos uno a la mitad inferior
        for (int i = split + 1; i <= fin; i++) {
            simbolos.get(i).codigo += "1";
        }

        // Recursivamente aplicar a cada mitad
        shannonFano(simbolos, inicio, split);
        shannonFano(simbolos, split + 1, fin);
    }
}
