// Experimento pra rodar e comparar todos os algoritmos sobre N labirintos
// aleatórios e agrega as métricas que a atividade pede -> qualidade (passos) e
// desempenho (nodos expandidos, fronteira, tempo, memória)
//
// Mais sobre o experimento que nós propomos:
//  - todos os algoritmos rodam no MESMO labirinto a cada iteração;
//  - só os labirintos COM solução entram nas médias de qualidade/desempenho
//    (os sem solução são contados a parte, pois "passos" não faz sentido neles);
//  - %Otimo = quantas vezes o algoritmo achou o MENOR caminho (igual ao do BFS).
//    Por padrão/construção, os algoritmos BFS e A* (heurística admissivel) sempre vão ser 100%.
//
// Uso: java ExperimentoBusca [N] [dim] [taxaObstaculos]
//   ex.: java ExperimentoBusca 2000 10 30

public class ExperimentoBusca {

    //acumulador de somas pra calcular medias de um algoritmo.
    static class Acumulador {
        String algoritmo;
        String heuristica;
        long somaPassos;
        long somaExpandidos;
        long somaFronteira;
        long somaTempoNs;
        long somaMemoria;
        int otimos;   // quantas vezes empatou o menor caminho (o do BFS)
        int n;        // quantos casos entraram

        void add(MetricasBusca m, int passosOtimo) {
            if (algoritmo == null) {
                algoritmo = m.getAlgoritmo();
                heuristica = m.getHeuristica();
            }
            somaPassos += m.getPassos();
            somaExpandidos += m.getNosExpandidos();
            somaFronteira += m.getFronteiraMaxima();
            somaTempoNs += m.getTempoNs();
            somaMemoria += m.getMemoriaBytes();
            if (m.getPassos() == passosOtimo) {
                otimos++;
            }
            n++;
        }

        String linha() {
            double d = (n == 0) ? 1 : n;
            return String.format("%-14s | %-10s | %7.2f | %10.1f | %12.1f | %9.4f | %10.0f | %6.1f%%",
                algoritmo,
                heuristica != null ? heuristica : "-",
                somaPassos / d,
                somaExpandidos / d,
                somaFronteira / d,
                (somaTempoNs / d) / 1_000_000.0,
                somaMemoria / d,
                100.0 * otimos / d);
        }
    }

    public static void main(String[] args) {

        int N   = args.length > 0 ? Integer.parseInt(args[0]) : 2000;
        int dim = args.length > 1 ? Integer.parseInt(args[1]) : 10;
        int tx  = args.length > 2 ? Integer.parseInt(args[2]) : 30;
        int warmup = 200;

        for (int i = 0; i < warmup; i++) {
            Labirinto lab = new Labirinto(dim, dim, tx, false);
            Busca b = new Busca(lab, false);
            b.buscarLargura();
            b.buscarProfundidade();
            b.buscarAEstrela(false);
            b.buscarAEstrela(true);
            b.buscarGuloso(false);
            b.buscarGuloso(true);
        }

        Acumulador accBfs = new Acumulador();
        Acumulador accDfs = new Acumulador();
        Acumulador accAM  = new Acumulador();
        Acumulador accAE  = new Acumulador();
        Acumulador accGM  = new Acumulador();
        Acumulador accGE  = new Acumulador();

        int solaveis = 0;
        int insolveis = 0;

        for (int i = 0; i < N; i++) {
            Labirinto lab = new Labirinto(dim, dim, tx, false);
            Busca b = new Busca(lab, false);

            b.buscarLargura();       MetricasBusca mBfs = b.getUltimaMetrica();
            b.buscarProfundidade();  MetricasBusca mDfs = b.getUltimaMetrica();
            b.buscarAEstrela(false); MetricasBusca mAM  = b.getUltimaMetrica();
            b.buscarAEstrela(true);  MetricasBusca mAE  = b.getUltimaMetrica();
            b.buscarGuloso(false);   MetricasBusca mGM  = b.getUltimaMetrica();
            b.buscarGuloso(true);    MetricasBusca mGE  = b.getUltimaMetrica();

            if (!mBfs.isEncontrou()) {
                insolveis++;
                continue; // labirinto sem soluçao: fora das médias de qualidade
            }

            solaveis++;
            int otimo = mBfs.getPassos(); // menor caminho de referência

            accBfs.add(mBfs, otimo);
            accDfs.add(mDfs, otimo);
            accAM.add(mAM, otimo);
            accAE.add(mAE, otimo);
            accGM.add(mGM, otimo);
            accGE.add(mGE, otimo);
        }

        System.out.println("===== Experimento BEE =====");
        System.out.printf("Labirintos: %d (%dx%d, %d%% obstaculos) | com solucao: %d | sem solucao: %d%n",
            N, dim, dim, tx, solaveis, insolveis);
        System.out.println("Medias sobre os labirintos COM solucao:");
        System.out.println();
        System.out.println(String.format("%-14s | %-10s | %7s | %10s | %12s | %9s | %10s | %6s",
            "Algoritmo", "Heuristica", "Passos", "Expandidos", "FronteiraMax",
            "Tempo(ms)", "Memoria(B)", "%Otimo"));
        System.out.println(accBfs.linha());
        System.out.println(accDfs.linha());
        System.out.println(accAM.linha());
        System.out.println(accAE.linha());
        System.out.println(accGM.linha());
        System.out.println(accGE.linha());
    }
}