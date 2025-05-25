package com.DogWalker.dog.walker.modelo.dtos;

public class ServicioDto {

        private String servicio;
        private String descripcion;
        private double precio;  // Nuevo campo para el precio
        private int id_servicio;

        public String getServicio() {
            return servicio;
        }

        public void setServicio(String servicio) {
            this.servicio = servicio;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public double getPrecio() {
            return precio;
        }

        public void setPrecio(double precio) {
            this.precio = precio;
        }

        public int getId_servicio() {
            return id_servicio;
        }

        public void setId_servicio(int id) {
            this.id_servicio = id;
        }
    }