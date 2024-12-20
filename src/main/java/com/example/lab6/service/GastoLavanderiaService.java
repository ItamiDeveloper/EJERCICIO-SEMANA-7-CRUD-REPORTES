package com.example.lab6.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.lab6.model.GastoLavanderia;
import com.example.lab6.repository.GastoLavanderiaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class GastoLavanderiaService {

    @Autowired
    private GastoLavanderiaRepository repository;

    public List<GastoLavanderia> obtenerGastos() {
        return repository.findAll();
    }

    public void registrarGasto(GastoLavanderia gasto) {
        repository.save(gasto);
    }

    public Optional<GastoLavanderia> buscarGasto(Integer id) {
        return repository.findById(id);
    }

    public void editarGasto(GastoLavanderia gasto) {
        repository.findById(gasto.getIdGasto()).ifPresent(gastoExistente -> {
            gastoExistente.setEmpleado(gasto.getEmpleado());
            gastoExistente.setInsumo(gasto.getInsumo());
            gastoExistente.setCantidadUsada(gasto.getCantidadUsada());
            gastoExistente.setCostoInsumo(gasto.getCostoInsumo());
            gastoExistente.setFecha(gasto.getFecha());
            gastoExistente.setObservaciones(gasto.getObservaciones());
            repository.save(gastoExistente);
        });
    }

    public void eliminarGasto(Integer id) {
        repository.deleteById(id);
    }
}
