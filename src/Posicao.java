/*
This file is part of Labirinto.

Labirinto is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or 
(at your option) any later version.

Labirinto is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Foobar.  If not, see <http://www.gnu.org/licenses/>. 
*/

/**
 * Classe que representa uma posição no labirinto
 */
public class Posicao{

	private int x;
	private int y;

	/**
	 * Construtor da classe. Inicializa com uma posição inválida (-1,-1).
	 */
	public Posicao(){
		x = -1;
		y = -1;
	}

	/**
	 * Construtor da classe. Inicializa com uma posição (X,Y).
	 */
	public Posicao(int x, int y){
		this.x = x;
		this.y = y;
	}

	/**
	 * Fornece a posição no eixo X
	 */
	public int getX(){
		return x;
	}

	/**
	 * Fornece a posição no eixo Y
	 */
	public int getY(){
		return y;
	}

	/**
	 * Atualiza a posição no eixo X
	 */
	public void setX(int x){
		this.x = x;
	}

	/**
	 * Atualiza a posição no eixo Y
	 */
	public void setY(int y){
		this.y = y;
	}

	/**
	 * Fornece a posição X,Y numa String.
	 */
	@Override
	public String toString(){
		return "(" + x + "," + y + ")";
	}

	/**
	 * Compara com outro objeto Posicao e devolve um booleano. Verdadeiro se as
	 * duas posições forem iguais, falso se for o contrário.
	 */
	public boolean comparaCom(Posicao p){
            return p != null && p.getX() == x & p.getY() == y;
	}


	/**
	 * Compara com uma posição (X,Y) e devolve um booleano. Verdadeiro se as
	 * duas posições forem iguais, falso se for o contrário.
	 */
	public boolean comparaCom(int x, int y){
            return this.x == x & this.y == y;
	}
}