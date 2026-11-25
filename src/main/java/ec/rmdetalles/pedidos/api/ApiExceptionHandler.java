package ec.rmdetalles.pedidos.api;

import ec.rmdetalles.pedidos.service.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail notFound(ResourceNotFoundException ex) { return problem(HttpStatus.NOT_FOUND,"No encontrado",ex.getMessage()); }
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail badRequest(IllegalArgumentException ex) { return problem(HttpStatus.BAD_REQUEST,"Solicitud no válida",ex.getMessage()); }
    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail conflict(IllegalStateException ex) { return problem(HttpStatus.CONFLICT,"Flujo no configurado",ex.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException ex) {
        String detail=ex.getBindingResult().getFieldErrors().stream().map(e->e.getField()+": "+e.getDefaultMessage()).collect(Collectors.joining("; "));
        return problem(HttpStatus.BAD_REQUEST,"Error de validación",detail);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail integrity(DataIntegrityViolationException ex) { return problem(HttpStatus.CONFLICT,"Conflicto de datos","La solicitud infringe una relación o restricción de datos."); }
    @ExceptionHandler(ResponseStatusException.class)
    ProblemDetail responseStatus(ResponseStatusException ex) { return problem(HttpStatus.valueOf(ex.getStatusCode().value()),"Solicitud no válida",ex.getReason()==null?"La solicitud no es válida.":ex.getReason()); }
    private ProblemDetail problem(HttpStatus s,String title,String detail) { ProblemDetail p=ProblemDetail.forStatusAndDetail(s,detail); p.setTitle(title); p.setType(URI.create("about:blank")); return p; }
}
