
public class Obstaculo {
	int id;
	int categoria;
	int dificultad;
	int tiempo;



public Obstaculo(int id, int categoria, int dificultad, int tiempo) {
	this.tiempo = tiempo;
	this.dificultad = dificultad;
	this.categoria = categoria;
	this.id = id;
}



public int getId() {
	return id;
}



public int getCategoria() {
	return categoria;
}



public int getDificultad() {
	return dificultad;
}



public int getTiempo() {
	return tiempo;
}


}