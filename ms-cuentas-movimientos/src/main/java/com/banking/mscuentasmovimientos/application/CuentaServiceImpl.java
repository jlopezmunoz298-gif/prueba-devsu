package com.banking.mscuentasmovimientos.application;

import com.banking.mscuentasmovimientos.application.dto.ApiResponse;
import com.banking.mscuentasmovimientos.application.dto.CuentaDTO;
import com.banking.mscuentasmovimientos.application.exception.CuentaInactivaException;
import com.banking.mscuentasmovimientos.application.exception.CuentaNotFoundException;
import com.banking.mscuentasmovimientos.application.port.interactor.ICuentaService;
import com.banking.mscuentasmovimientos.application.port.output.agent.IClienteAgentPort;
import com.banking.mscuentasmovimientos.application.port.output.db.CuentaRepositoryPort;
import com.banking.mscuentasmovimientos.domain.entity.Cuenta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements ICuentaService {

    private final CuentaRepositoryPort repo;
    private final IClienteAgentPort clienteAgent;

    @Override
    public ApiResponse<CuentaDTO> crearCuenta(CuentaDTO dto) {
        var cliente = clienteAgent.obtenerClientePorIdentificacion(dto.getClienteIdentificacion());

        Cuenta cuenta = toDomain(dto);
        cuenta.setClienteId(cliente.getId());

        Cuenta saved = repo.save(cuenta);
        CuentaDTO response = toDTO(saved, false);
        response.setClienteIdentificacion(cliente.getIdentificacion());
        response.setNombreCliente(cliente.getNombre());

        return new ApiResponse<>("Cuenta creada correctamente", response);
    }

    @Override
    public ApiResponse<CuentaDTO> obtenerCuentaPorId(Long id) {
        Cuenta cuenta = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException(id));

        if (!cuenta.getEstado()) {
            throw new CuentaInactivaException(id);
        }

        var cliente = clienteAgent.obtenerClientePorId(cuenta.getClienteId());

        CuentaDTO response = toDTO(cuenta, false);
        response.setClienteIdentificacion(cliente.getIdentificacion());
        response.setNombreCliente(cliente.getNombre());

        return new ApiResponse<>("Cuenta obtenida correctamente", response);
    }

    @Override
    public List<ApiResponse<CuentaDTO>> obtenerTodasLasCuentas() {
        return repo.findAll().stream()
                .filter(Cuenta::getEstado)
                .map(cuenta -> {
                    var cliente = clienteAgent.obtenerClientePorId(cuenta.getClienteId());

                    CuentaDTO dto = toDTO(cuenta, false);
                    dto.setClienteIdentificacion(cliente.getIdentificacion());
                    dto.setNombreCliente(cliente.getNombre());

                    return new ApiResponse<>("Cuenta encontrada", dto);
                })
                .toList();
    }

    @Override
    public ApiResponse<CuentaDTO> actualizarCuenta(Long id, CuentaDTO dto) {
        Cuenta existente = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException(id));

        if (!existente.getEstado()) {
            throw new CuentaInactivaException(id);
        }

        actualizarFrom(existente, dto);
        Cuenta actualizado = repo.save(existente);

        var cliente = clienteAgent.obtenerClientePorIdentificacion(dto.getClienteIdentificacion());

        CuentaDTO response = toDTO(actualizado, false);
        response.setClienteIdentificacion(cliente.getIdentificacion());
        response.setNombreCliente(cliente.getNombre());

        return new ApiResponse<>("Cuenta actualizada correctamente", response);
    }

    @Override
    public ApiResponse<CuentaDTO> eliminarCuenta(Long id) {
        Cuenta existente = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException(id));

        if (!existente.getEstado()) {
            throw new CuentaInactivaException(id);
        }

        existente.setEstado(false);
        Cuenta actualizado = repo.save(existente);

        var cliente = clienteAgent.obtenerClientePorId((id));
        CuentaDTO response = toDTO(actualizado, false);
        response.setClienteIdentificacion(cliente.getIdentificacion());
        response.setNombreCliente(cliente.getNombre());


        return new ApiResponse<>("Cuenta deshabilitada correctamente", response);
    }

    // ---------- Helpers ----------
    private Cuenta toDomain(CuentaDTO dto) {
        return Cuenta.builder()
                .id(dto.getId())
                .numeroCuenta(dto.getNumeroCuenta())
                .tipoCuenta(dto.getTipoCuenta())
                .saldoInicial(dto.getSaldoInicial())
                .estado(dto.getEstado())
                .clienteId(dto.getClienteId())
                .build();
    }

    private CuentaDTO toDTO(Cuenta cuenta, boolean enrichWithCliente) {
        CuentaDTO dto = CuentaDTO.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getClienteId())
                .build();

        if (enrichWithCliente) {
            var cliente = clienteAgent.obtenerClientePorIdentificacion(dto.getClienteIdentificacion());
            dto.setClienteIdentificacion(cliente.getIdentificacion());
            dto.setNombreCliente(cliente.getNombre());
        }
        return dto;
    }

    private void actualizarFrom(Cuenta existente, CuentaDTO dto) {
        existente.setNumeroCuenta(dto.getNumeroCuenta());
        existente.setTipoCuenta(dto.getTipoCuenta());
        existente.setSaldoInicial(dto.getSaldoInicial());
        existente.setEstado(dto.getEstado());
    }


}
