// Demonstração da infraestrutura de busca
// Cria um labirinto 10x10 com 30% de obstáculos (mesmos parâmetros do ExemploLabirinto), executa a busca e imprime o labirinto com o caminho encontrado, seguido das métricas da execução.
public class ExemploBusca {

    public static void main(String[] args) {

        boolean debug = false;

        Labirinto labirinto = new Labirinto(10, 10, 30, debug);

        System.out.println("Labirinto gerado:");
        labirinto.print(null);

        Posicao entrada = labirinto.getPosicaoAtual();
        Posicao saida = labirinto.getPosicaoSaida();
        System.out.println("Entrada: " + entrada + " | Saida: " + saida
                + " | DLR: " + labirinto.getDLR(entrada, saida));

        Busca busca = new Busca(labirinto, debug);
        Posicao[] caminho = busca.buscarLargura();

        System.out.println("\nResultado da busca:");
        if (caminho == null) {
            System.out.println("Nenhum caminho encontrado entre entrada e saida.");
            labirinto.print(null);
        } else {
            labirinto.print(caminho);
        }

        System.out.println("\nMetricas:");
        System.out.println(busca.getUltimaMetrica());
    }
}