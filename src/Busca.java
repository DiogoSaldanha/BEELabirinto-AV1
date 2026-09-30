import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;

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
        return buscarLargura();
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
    private long memoriaUsada() {
        Runtime rt = Runtime.getRuntime();
        System.gc();
        return rt.totalMemory() - rt.freeMemory();
    }

    // Métricas da última busca executada (null se nenhuma rodou ainda).
    public MetricasBusca getUltimaMetrica() {
        return ultimaMetrica;
    }
}