/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.file.service.legacy.internal;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.rcenvironment.core.authentication.User;
import de.rcenvironment.core.communication.fileaccess.api.RemoteFileConnection.FileType;
import de.rcenvironment.core.datamanagement.FileDataService;
import de.rcenvironment.core.datamanagement.backend.MetaDataBackendService;
import de.rcenvironment.core.utils.common.security.AllowRemoteAccess;
import de.rcenvironment.core.utils.incubator.Assertions;

/**
 * 
 * Implementation of the {@link FileService}.
 * 
 * @author Doreen Seider
 */
@Deprecated
public class FileServiceImpl implements FileService {

    private static final String PARAMETER_UUID = "uuid";

    private static final String ERROR_INPUT_STREAM_NOT_FOUND = "The input stream could not be found: ";

    private static final String ERROR_PARAMETERS_NULL = "The parameter \"{0}\" must not be null.";

    private static final Log LOGGER = LogFactory.getLog(FileServiceImpl.class);

    /**
     * The {@link Map} to store the open {@link InputStream} objects.
     */
    private static Map<String, InputStream> inputStreamMap = Collections.synchronizedMap(new HashMap<String, InputStream>());

    private FileDataService dataService;

    private MetaDataBackendService metaDataBackendService;

    protected void bindMetadataBackendService(MetaDataBackendService newService) {
        metaDataBackendService = newService;
    }

    protected void bindFileDataService(FileDataService newFileDataService) {
        dataService = newFileDataService;
    }

    @Override
    @AllowRemoteAccess
    public void close(String uuid) throws IOException {
        Assertions.isDefined(uuid, MessageFormat.format(ERROR_PARAMETERS_NULL, PARAMETER_UUID));
        if (inputStreamMap.containsKey(uuid)) {
            inputStreamMap.get(uuid).close();
            inputStreamMap.remove(uuid);
        }
    }

    @Override
    @AllowRemoteAccess
    public String open(User user, FileType type, String uri) throws IOException {

        Assertions.isDefined(type, MessageFormat.format(ERROR_PARAMETERS_NULL, "type"));
        Assertions.isDefined(uri, MessageFormat.format(ERROR_PARAMETERS_NULL, "uri"));

        LOGGER.debug("Received request for file: " + uri);
        String uuid = UUID.randomUUID().toString();

        if (type == FileType.RCE_DM) {
            InputStream inputStream = dataService.getStreamFromDataReference(user,
                metaDataBackendService.getDataReference(uri), false);

            if (inputStream == null) {
                throw new IOException("The file could not be found in the RCE data management: " + uri);
            }
            inputStreamMap.put(uuid, inputStream);

        } else {
            throw new IOException("Only the RCE data management and the file system are supported.");
        }

        return uuid;
    }

    @Override
    public int read(String uuid) throws IOException {

        Assertions.isDefined(uuid, MessageFormat.format(ERROR_PARAMETERS_NULL, PARAMETER_UUID));

        InputStream inputStream = inputStreamMap.get(uuid);
        if (inputStream == null) {
            throw new IOException(ERROR_INPUT_STREAM_NOT_FOUND + uuid);
        } else {
            return inputStream.read();
        }
    }

    @Override
    @AllowRemoteAccess
    public byte[] read(String uuid, Integer len) throws IOException {

        Assertions.isDefined(uuid, MessageFormat.format(ERROR_PARAMETERS_NULL, PARAMETER_UUID));
        Assertions.isDefined(len, MessageFormat.format(ERROR_PARAMETERS_NULL, "len"));

        InputStream inputStream = inputStreamMap.get(uuid);
        if (inputStream == null) {
            throw new IOException(ERROR_INPUT_STREAM_NOT_FOUND + uuid);
        } else {
            byte[] buffer = new byte[len];
            int nRead = inputStream.read(buffer, 0, len);
            if (nRead == len) {
                return buffer;
            } else if (nRead < 0) {
                // FIXME: must not be closed here. remove if side effects are known and handle them if needed -seid_do
                inputStream.close();
                inputStreamMap.remove(uuid);
                return new byte[0];
            } else {
                byte[] resultBuffer = new byte[nRead];
                System.arraycopy(buffer, 0, resultBuffer, 0, nRead);
                return resultBuffer;
            }
        }
    }

    @Override
    public long skip(String uuid, Long n) throws IOException {

        Assertions.isDefined(uuid, MessageFormat.format(ERROR_PARAMETERS_NULL, PARAMETER_UUID));
        Assertions.isDefined(n, MessageFormat.format(ERROR_PARAMETERS_NULL, "n"));

        InputStream inputStream = inputStreamMap.get(uuid);
        if (inputStream == null) {
            throw new IOException(ERROR_INPUT_STREAM_NOT_FOUND + uuid);
        } else {
            return inputStream.skip(n.longValue());
        }
    }

}
