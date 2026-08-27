import java.util.ArrayList;

/**
 * Classe que implementa o conceito de um nodo em uma árvore.
 */
public class Nodo{

	private Nodo pai;
	private ArrayList<Nodo> filho;
	private Object valor;
	private int profundidade;

	/**
	 * Construtor
	 *
	 * @param p Nodo pai deste nodo.
	 * @param v Valor do nodo. Pode ser qualquer objeto.
	 */
	public Nodo (Nodo p, Object v){
		pai   = p;
		filho = new ArrayList<>();
		valor = v;

		if (pai != null) {
			profundidade = pai.getProfundidade() + 1;
			p.setFilho(this);
		}
		else 
			profundidade = 0;
	}

	/**
	 * Altera o pai do nodo.
	 *
	 * @param p Novo pai. Readecua a profundidade do nodo para a do novo pai + 1.
	 */
	public void setPai(Nodo p){
		pai = p;
		profundidade = pai.getProfundidade() + 1;
	}

	/**
	 * Adiciona um nodo filho.
	 *
	 * @param f Novo pai. Readecua a profundidade do nodo para a do novo pai + 1.
	 */
	public void setFilho(Nodo f){
		f.setPai(this);
		filho.add(f);
	}

	/**
	 * Altera o pai do nodo.
	 *
	 * @param o Novo pai. Readecua a profundidade do nodo para a do novo pai + 1.
	 */
	public void setValor(Object o){
		valor = o;
	}

	/**
	 * Busca nodo pai.
	 */
	public Nodo getPai(){
		return pai;
	}

	/**
	 * Busca nodo filho.
	 */
	public Nodo getFilho(){
		if (!filho.isEmpty())
			return filho.get(0);
		else
			return null;
	}

	/**
	 * Busca nodo irmão de acordo com o critério do nodo pai.
	 */
	public Nodo getIrmao(){
		if (pai != null) {
			Nodo irmao = pai.getIrmao(this);
			return irmao;
		} else
			return null;
	}

	/**
	 * Busca nodo irmão de um dado filho. Neste caso o próximo no vetor de filhos.
	 */
	public Nodo getIrmao(Nodo i){
		int posFilho = filho.indexOf(i);
		if (filho.size() > posFilho+1) {
			Nodo irmao = filho.get(posFilho+1);
			return irmao;
		} else
			return null;
	}

	/**
	 * Fornece o objeto valor do nodo
	 */
	public Object getValor(){
		return valor;
	}

	/**
	 * Fornece a profundidade do nodo.
	 */
	public int getProfundidade(){
		return profundidade;
	}
}