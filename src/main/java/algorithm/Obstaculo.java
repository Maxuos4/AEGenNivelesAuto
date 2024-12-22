package algorithm;

public class Obstaculo {
	private String nombre;
	private final int id;
	private final Categoria categoria;
	private final int dificultad;
	private final int tiempo;

	public Obstaculo(String nombre, int id, int categoria, int dificultad, int tiempo) {
		this.nombre = nombre;
		this.id = id;
		this.categoria = Categoria.values()[categoria];
		this.tiempo = tiempo;
		this.dificultad = dificultad;
	}

	@Override
	public String toString() {
		return "Obstaculo{" +
				"nombre='" + nombre + '\'' +
				", id=" + id +
				", categoria=" + categoria +
				", tiempo=" + tiempo +
				", dificultad=" + dificultad +
				'}';
	}
	public int getId() {
		return id;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public int getDificultad() {
		return dificultad;
	}

	public int getTiempo() {
		return tiempo;
	}
}