/*This file is part of Labirinto.

Labirinto is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or 
(at your option) any later version.

Labirinto is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Foobar.  If not, see <http://www.gnu.org/licenses/>. */

/**
 * A inerface Busca implementa os algoritmos para busca em espaço de estados. Ela
 * mantém a estrutura de árvore na qual são feitas as inferências sobre os nodos.
 */
public abstract class AbstractBusca{
    
    Labirinto labirinto;
    Nodo raiz;
    boolean debug;

    /**
     * Construtor do algoritmo de busca. Mantém uma referência para a 
     * implementação do labirinto (o problema), a raiz da árevore de estados e a
     * flag para debug.
     *
     * @param l referência para a implementação do labirinto.
     * @param d ativa(true)/desativa(false) o debug.
     */
    public AbstractBusca(Labirinto l, boolean d){
	labirinto = l; 
	raiz = new Nodo(null, labirinto.getPosicaoAtual());
	debug = d;
    }

    /**
     * Executa busca pelo método selecionado.  
     * 
     * @param aEstrela Flag para o tipo de busca selecionado. Com informação
     * heurística e custo (A*) ou somente heurística (guloso)
     */
    public abstract Posicao[] buscar(boolean aEstrela, boolean aEstrelaAlt);


	/**
     * Expande recursivamene o espaço de estados atual com base no critério de 
     * busca. Devolve uma reverência para nodo solução encontrado no espaço de
     * estados.
     *
     * @param n nodo que deve ser expandido.
     * @param aEstrela Flag para o tipo de busca selecionado. Com informação
     * heurística e custo (A*) ou somente heurística (guloso)
     * @return devolve uma referência para nodo solução encontrado no espaço de
     * estados.
     */
    public abstract Nodo expandir(Nodo n, boolean aEstrela, boolean aEstrelaAlt);
    
    
}