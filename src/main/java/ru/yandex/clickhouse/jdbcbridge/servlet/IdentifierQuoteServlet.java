package ru.yandex.clickhouse.jdbcbridge.servlet;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;
import ru.yandex.clickhouse.jdbcbridge.db.jdbc.BridgeConnectionManager;
import ru.yandex.clickhouse.settings.ClickHouseProperties;
import ru.yandex.clickhouse.util.ClickHouseRowBinaryStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;

/**
 * Created by krash on 21.09.18.
 */
@Data
@Slf4j
public class IdentifierQuoteServlet extends HttpServlet {

    private final BridgeConnectionManager manager;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = manager.get(req.getParameter("connection_string"))) {
            ClickHouseRowBinaryStream stream = new ClickHouseRowBinaryStream(resp.getOutputStream(), null, new ClickHouseProperties());
            final String identifierQuoteString = connection.getMetaData().getIdentifierQuoteString();
            resp.setContentType("application/octet-stream");
            stream.writeString(identifierQuoteString);
        } catch (Exception err) {
            log.error(err.getMessage(), err);
            resp.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500, err.getMessage());
        }
    }
}
