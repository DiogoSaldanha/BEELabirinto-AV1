import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.PriorityQueue;

/**
 * Nossa implementação de AbstractBusca para o problema do labirinto.
 *
 * Basicamente é a infraestrutura reutilizável que todos os algoritmos vão compartilhar:
 *   -> controle de posições visitadas (para não entrar em laço, já que "Labirinto.getExpansao" devolve todos os vizinhos livres, inclusive o que de onde recém viemos);
 *   -> reconstrução do caminho a partir do nó-solução, subindo pelos pais;
 *   -> coleta de métricas (a partir de "MetricasBusca");
 *   -> um motor de busca genérico como parâmetro FIFO, LIFO, prioridade, etc ... .
 *
 * Já vem uma Busca em Largura funcional. As outras estratégias (DFS e guloso) reaproveitam o mesmo motor, trocando só a política da fronteira.
 *
 * Não alteramos a API das classes originais do projeto.
 */

public class Busca extends AbstractBusca {

    // Marca das posições já geradas/visitadas
    private boolean[][] visitado;

    // Política de inserção na fronteira do motor atual (FIFO, LIFO, prioridade...).
    private Consumer<Nodo> aoGerarFilho;

    // Contador de nós expandidos  
    private int nosExpandidos;

    // Maior tamanho atingido pela fronteira na execução corrente.
    private int fronteiraMaxima;

    // Métricas da última busca executada.
    private MetricasBusca ultimaMetrica;

    //Construtor
    public Busca(Labirinto l, boolean d) {
        super(l, d);
    }

    // Métodos abstratos exigidos por AbstractBusca

        @Override
        public Posicao[] buscar(boolean aEstrela, boolean aEstrelaAlt) {
            // aEstrela = usa o custo g acumulado (A*); senao guloso (so a heuristica).
            // aEstrelaAlt = usa a heuristica alternativa (Euclidiana); senao Manhattan.
            boolean euclidiana = aEstrelaAlt;
            if (aEstrela) {
                return buscarAEstrela(euclidiana);
            }
            return buscarGuloso(euclidiana);
        }

    // Basicamente expande um nó, gerando os filhos (vizinhos livres ainda não visitados), marcando-os como visitados e entregando à fronteira corrente via "aoGerarFilho". 
    // Se algum filho gerado for a saída, devolve esse nó-solução; caso contrário, devolve NULL.
    @Override
    public Nodo expandir(Nodo n, boolean aEstrela, boolean aEstrelaAlt) {
        Posicao pos = (Posicao) n.getValor();
        Posicao saida = labirinto.getPosicaoSaida();
        nosExpandidos++;

        for (Posicao viz : labirinto.getExpansao(pos)) {
            int vx = viz.getX();
            int vy = viz.getY();
            if (visitado[vx][vy]) {
                continue;
            }
            visitado[vx][vy] = true;

            //o construtor de Nodo já registra esse nó aqui como filho de n.
            Nodo filho = new Nodo(n, viz);

            if (viz.comparaCom(saida)) {
                return filho; // solução encontrada no momento da geração
            }
            if (aoGerarFilho != null) {
                aoGerarFilho.accept(filho);
            }
        }
        return null;
    }

    // Algoritmos

    // Busca em Largura -Fronteira FIFO, garante o caminho de menor número de passos em grafo
    public Posicao[] buscarLargura() {
        ArrayDeque<Nodo> fila = new ArrayDeque<Nodo>();
        return executar("BFS", null, fila::addLast, fila::pollFirst, fila::size, fila::isEmpty);
    }

    // Busca em Profundidade - Fronteira LIFO (pilha)
    // Acha um caminho se existir, mas nao garante o de menor numero de passos (dai o contraste de qualidade com o BFS).
    // Usa o mesmo motor e a mesma expansao do BFS; muda só a ordem em que a fronteira devolve os nós
    public Posicao[] buscarProfundidade() {
        ArrayDeque<Nodo> pilha = new ArrayDeque<Nodo>();
        return executar("DFS", null, pilha::addFirst, pilha::pollFirst, pilha::size, pilha::isEmpty);
    }

        // Busca com informacao: A* (custo g + heuristica). Otimo pra heuristica admissivel.
    public Posicao[] buscarAEstrela(boolean euclidiana) {
        return executarInformada("A*", true, euclidiana);
    }

    // Busca com informacao: Guloso (so a heuristica, ignora o custo g). Rapido, mas nao garante o menor caminho.
    public Posicao[] buscarGuloso(boolean euclidiana) {
        return executarInformada("Guloso", false, euclidiana);
    }

    // Motor genérico de busca

    // Motor de busca compartilhado. A estratégia é definida pelas operações da
    // fronteira passadas por parâmetro (inserir, remover, tamanho, vazio).
    private Posicao[] executar(String nomeAlgoritmo,
                               String heuristica,
                               Consumer<Nodo> inserir,
                               java.util.function.Supplier<Nodo> remover,
                               java.util.function.IntSupplier tamanho,
                               java.util.function.BooleanSupplier vazia) {

        reiniciar();
        this.aoGerarFilho = inserir;

        MetricasBusca m = new MetricasBusca(nomeAlgoritmo);
        m.setHeuristica(heuristica);

        Posicao entrada = labirinto.getPosicaoAtual();
        Posicao saida = labirinto.getPosicaoSaida();

        // raiz nova a cada execução, pra não acumular filhos entre as buscas
        Nodo raizLocal = new Nodo(null, entrada);

        long memAntes = memoriaUsada();
        long t0 = System.nanoTime();

        Nodo solucao = null;
        visitado[entrada.getX()][entrada.getY()] = true;

        if (entrada.comparaCom(saida)) {
            solucao = raizLocal;
        } else {
            inserir.accept(raizLocal);
            while (!vazia.getAsBoolean() && solucao == null) {
                int tam = tamanho.getAsInt();
                if (tam > fronteiraMaxima) {
                    fronteiraMaxima = tam;
                }
                Nodo n = remover.get();
                solucao = expandir(n, false, false);
            }
        }

        long t1 = System.nanoTime();
        long memDepois = memoriaUsada();

        Posicao[] caminho = reconstruirCaminho(solucao);

        m.setEncontrou(solucao != null);
        m.setPassos(caminho != null ? caminho.length - 1 : -1);
        m.setNosExpandidos(nosExpandidos);
        m.setFronteiraMaxima(fronteiraMaxima);
        m.setTempoNs(t1 - t0);
        m.setMemoriaBytes(Math.max(0L, memDepois - memAntes));
        this.ultimaMetrica = m;

        return caminho;
    }

    
    // Motor das buscas COM informacao (A* e guloso). Fronteira = fila de prioridade ordenada por f.
    // Diferente do motor sem informacao, aqui o teste de objetivo e a marca de visitado são feitos
    // na REMOÇÃO do no (pop), nao na geracao, por isso basicamente o A* fica melhor otimizado.
    private Posicao[] executarInformada(String nomeAlgoritmo, boolean usarCusto, boolean euclidiana) {

        reiniciar();

        MetricasBusca m = new MetricasBusca(nomeAlgoritmo);
        m.setHeuristica(euclidiana ? "Euclidiana" : "Manhattan");

        Posicao entrada = labirinto.getPosicaoAtual();
        Posicao saida = labirinto.getPosicaoSaida();

        Nodo raizLocal = new Nodo(null, entrada);

        long memAntes = memoriaUsada();
        long t0 = System.nanoTime();

        Nodo solucao = null;

        if (entrada.comparaCom(saida)) {
            solucao = raizLocal;
        } else {
            PriorityQueue<Nodo> fronteira = new PriorityQueue<Nodo>(
                (a, b) -> Double.compare(custoF(a, usarCusto, euclidiana),
                                         custoF(b, usarCusto, euclidiana)));
            fronteira.add(raizLocal);

            while (!fronteira.isEmpty() && solucao == null) {
                if (fronteira.size() > fronteiraMaxima) {
                    fronteiraMaxima = fronteira.size();
                }
                Nodo n = fronteira.poll();
                Posicao pos = (Posicao) n.getValor();

                // ignora copias obsoletas do mesmo estado que sobraram na fila
                if (visitado[pos.getX()][pos.getY()]) {
                    continue;
                }
                visitado[pos.getX()][pos.getY()] = true;
                nosExpandidos++;

                // teste de objetivo na remocao (essencial pra otimalidade do A*)
                if (pos.comparaCom(saida)) {
                    solucao = n;
                    break;
                }

                for (Posicao viz : labirinto.getExpansao(pos)) {
                    if (!visitado[viz.getX()][viz.getY()]) {
                        fronteira.add(new Nodo(n, viz));
                    }
                }
            }
        }

        long t1 = System.nanoTime();
        long memDepois = memoriaUsada();

        Posicao[] caminho = reconstruirCaminho(solucao);

        m.setEncontrou(solucao != null);
        m.setPassos(caminho != null ? caminho.length - 1 : -1);
        m.setNosExpandidos(nosExpandidos);
        m.setFronteiraMaxima(fronteiraMaxima);
        m.setTempoNs(t1 - t0);
        m.setMemoriaBytes(Math.max(0L, memDepois - memAntes));
        this.ultimaMetrica = m;

        return caminho;
    }

    private double custoF(Nodo n, boolean usarCusto, boolean euclidiana) {
        Posicao p = (Posicao) n.getValor();
        double h = heuristica(p, euclidiana);
        double g = n.getProfundidade();
        return usarCusto ? g + h : h;
    }

    // Heuristicas admissiveis pra grade 4-direcional:
    //  - Manhattan: |dx| + |dy| (mais informada nessa grade)
    //  - Euclidiana: distância em linha reta (a DLR já é fornecida pelo Labirinto)
    private double heuristica(Posicao p, boolean euclidiana) {
        Posicao saida = labirinto.getPosicaoSaida();
        if (euclidiana) {
            return labirinto.getDLR(p, saida);
        }
        return Math.abs(p.getX() - saida.getX()) + Math.abs(p.getY() - saida.getY());
    }

    
    // Helpers

    // Reinicia o estado interno pra uma nova execução
    private void reiniciar() {
        this.visitado = new boolean[labirinto.getDimX()][labirinto.getDimY()];
        this.nosExpandidos = 0;
        this.fronteiraMaxima = 0;
        this.aoGerarFilho = null;
    }

    // Reconstrói o caminho subindo do nó-solução até a raiz pelos pais e invertendo a ordem (entrada -> ... -> saída).
    private Posicao[] reconstruirCaminho(Nodo solucao) {
        if (solucao == null) {
            return null;
        }
        ArrayList<Posicao> caminho = new ArrayList<Posicao>();
        Nodo atual = solucao;
        while (atual != null) {
            caminho.add((Posicao) atual.getValor());
            atual = atual.getPai();
        }
        Collections.reverse(caminho);
        return caminho.toArray(new Posicao[0]);
    }


    // Estimativa de memória em uso no heap (bytes)
    // so faz sentido agregada (media no ExperimentoBusca). Sem System.gc(): aí ele deixava o
    // experimento lento e ainda contaminava a medição de tempo.
    private long memoriaUsada() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    // Métricas da última busca executada (null se nenhuma rodou ainda).
    public MetricasBusca getUltimaMetrica() {
        return ultimaMetrica;
    }
}