package util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.Server;

// TODO: Auto-generated Javadoc
/**
 * DirectoryWatcher monitors the directory of the server and triggers a
 * notification if there is any change in the directory contents
 * 
 * Reference:
 * https://docs.oracle.com/javase/tutorial/essential/io/notification.html
 * 
 * @author Shantanoo
 * 
 */
public class DirectoryWatcher implements Runnable {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(DirectoryWatcher.class);

	/** The client. */
	private Server server;

	/**
	 * Instantiates a new directory watcher.
	 *
	 * @param server
	 *            the peer
	 */
	public DirectoryWatcher(Server server) {
		this.server = server;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Path dir = Paths.get(server.getFilesDirectory().toURI());
			logger.info("[" + server.getId() + "] Watching files directory:" + dir + " for Client:" + server.getId());
			
			WatchService watchService = FileSystems.getDefault().newWatchService();
			WatchKey WatcherKey = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
			
			for (;;) {
				try {
					WatcherKey = watchService.take();
				} catch (InterruptedException x) {
					return;
				}
				for (WatchEvent<?> eventValue : WatcherKey.pollEvents()) {
					WatchEvent.Kind<?> kind_event = eventValue.kind();
					Path fileName = (Path) eventValue.context();
					if (kind_event == StandardWatchEventKinds.OVERFLOW) {
						continue;
					}

					// Update the Server on file\directory delete events
					if (kind_event == StandardWatchEventKinds.ENTRY_DELETE) {
						server.deleteFile(fileName.toString());
					}

					// Update the Server on file\directory modify events
					if (kind_event == StandardWatchEventKinds.ENTRY_MODIFY) {
						server.deleteFile(fileName.toString());
						server.addFile(fileName.toString());
					}

					// Update the Index Server on file\directory create event
					if (kind_event == StandardWatchEventKinds.ENTRY_CREATE) {
						server.addFile(fileName.toString());
					}
				}
				boolean validvalue = WatcherKey.reset();
				if (!validvalue) {
					break;
				}
			}
		} catch (IOException x) {
			logger.error("[" + server.getId() + "] Exception: Unable to watch directory events.\n" + x);
		}
	}
}