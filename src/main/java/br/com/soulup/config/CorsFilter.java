package br.com.soulup.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Libera o front-end (outro dominio) para chamar a API pelo navegador (CORS).
 * Origens permitidas: http://localhost:5173 (Vite local) e a URL da variavel FRONT_URL
 * (front publicado na Vercel). FRONT_URL aceita mais de uma URL separada por virgula.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    private final List<String> origensPermitidas = new ArrayList<>();

    public CorsFilter() {
        origensPermitidas.add("http://localhost:5173");
        String frontUrl = System.getenv("FRONT_URL");
        if (frontUrl != null && !frontUrl.isBlank()) {
            for (String url : frontUrl.split(",")) {
                // remove barra no final: "https://x.vercel.app/" -> "https://x.vercel.app"
                origensPermitidas.add(url.trim().replaceAll("/+$", ""));
            }
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origem = request.getHeader("Origin");
        if (origem != null && origensPermitidas.contains(origem)) {
            response.setHeader("Access-Control-Allow-Origin", origem);
            response.setHeader("Vary", "Origin");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type");
            response.setHeader("Access-Control-Max-Age", "3600");
        }

        // Requisicao "preflight" do navegador: responde na hora, sem passar pelos Resources
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }
}
