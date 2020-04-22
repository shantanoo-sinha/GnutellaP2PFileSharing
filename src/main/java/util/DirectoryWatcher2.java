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

import client.Client;
import client.Client2;

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
public class DirectoryWatcher2 implements Runnable {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(DirectoryWatcher2.class);

	/** The client. */
	private Client2 client;

	/**
	 * Instantiates a new directory watcher.
	 *
	 * @param client the client
	 */
	public DirectoryWatcher2(Client2 client) {
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Path masterFileDir = Paths.get(client.getMasterFilesDirectory().toURI());
			logger.info("[" + client.getId() + "] Watching master files directory:" + masterFileDir + " for Client:" + client.getId());
			
			WatchService watchService = FileSystems.getDefault().newWatchService();
			WatchKey watcherKey1 = masterFileDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
			
			for (;;) {
				try {
					watcherKey1 = watchService.take();
				} catch (InterruptedException x) {
					return;
				}
				for (WatchEvent<?> eventValue : watcherKey1.pollEvents()) {
					WatchEvent.Kind<?> kind_event = eventValue.kind();
					Path fileName = (Path) eventValue.context();
					if (kind_event == StandardWatchEventKinds.OVERFLOW) {
						continue;
					}

					// Update the Server on file\directory delete events
					if (kind_event == StandardWatchEventKinds.ENTRY_DELETE) {
						/*server.deleteFile(fileName.toString());*/
//						client.deleteMasterFile(fileName.toString());
						logger.info("ENTRY_DELETE");
					}

					// Update the Server on file\directory modify events
					if (kind_event == StandardWatchEventKinds.ENTRY_MODIFY) {
						/*server.deleteFile(fileName.toString());
						server.addFile(fileName.toString());*/
						/*client.deleteMasterFile(fileName.toString());
						client.addMasterFile(fileName.toString());*/
						logger.info("ENTRY_MODIFY");
						client.modifyMasterFile(fileName.toString());
					}

					// Update the Index Server on file\directory create event
					if (kind_event == StandardWatchEventKinds.ENTRY_CREATE) {
						/*server.addFile(fileName.toString());*/
//						client.addMasterFile(fileName.toString());
						logger.info("ENTRY_CREATE");
					}
				}
				
				boolean validvalue = watcherKey1.reset();
				if (!validvalue) {
					break;
				}
			}
		} catch (IOException x) {
			logger.error("[" + client.getId() + "] Exception: Unable to watch directory events.\n" + x);
		}
	}
}