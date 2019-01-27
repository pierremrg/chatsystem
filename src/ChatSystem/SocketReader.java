package ChatSystem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Path;
import java.util.Base64;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Permet de lire les messages recus
 */
public class SocketReader extends Thread {

	private Socket socket;
	private Controller controller;
	private volatile Group group = null;

	/**
	 * Cree un SocketReader (thread)
	 * @param name Nom du thread
	 * @param socket Socket a utiliser
	 * @param controller Controller de l'application
	 */
	public SocketReader(String name, Socket socket, Controller controller) {
		super(name);
		this.socket = socket;
		this.controller = controller;
	}

	/**
	 * Thread qui permet de lire les messages recus
	 */
	@Override
	public void run() {

		try {

			Message message = null;
			//System.out.println("SocketReader connected...");

			BufferedReader in_data = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Recupere les donnees recues
			String stringData = in_data.readLine();

			// Tant que la connexion est ouverte, on lit les messages
			while (stringData != null) {

				// On decode le message recu et on l'envoie au controller
				message = decodeMessageFromString(stringData);
				
				if (group == null)
					group = message.getReceiverGroup();
				
				// Reception d'un fichier ou d'une image
				if(message.getFunction() == Message.FUNCTION_FILE || message.getFunction() == Message.FUNCTION_IMAGE) {
					
					String newFileString = (message.getFunction() == Message.FUNCTION_IMAGE) ? "une nouvelle image" : "un nouveau fichier";
					
					// On demande a l'utilisateur s'il veut enregistrer le message
					int dialogResult = JOptionPane.showConfirmDialog(null,
							"Vous avez recu " + newFileString + " de la part de " + message.getSender().getUsername() +
							".\nVoulez-vous l'enregistrer ?", 
							"Nouveau fichier recu", JOptionPane.YES_NO_OPTION);
					
					// On lit le deuxieme message recu (le fichier)
					String fileData = in_data.readLine();
					
					if(dialogResult == JOptionPane.YES_OPTION) {
					
						File sentFile = new File(message.getContent());
						
						// Selection de l'emplacement ou enregistrer le fichier
						JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
						chooser.setDialogTitle("Selectionner ou enregistrer le fichier");
						chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						chooser.setMultiSelectionEnabled(false);
						chooser.setSelectedFile(sentFile);
						int returnValue = chooser.showSaveDialog(null);
						
						// On enregistre que si l'utilisateur le veut
						if(returnValue == JFileChooser.APPROVE_OPTION) {
							File selectedFile = chooser.getSelectedFile();
	
							decodeAndSaveFileFromString(fileData, selectedFile.toPath());
							message.setContent(selectedFile.toString());
						}
					
					}
					
				}

				controller.receiveMessage(message);
				
				// Lecture du prochain message
				stringData = in_data.readLine();
			
			}
			
		} catch (SocketException e) {

			// Socket deja ferme : pas d'erreur

		} catch (Exception e) {
			GUI.showError("Erreur dans la lecture des messages recus.");
			
		} finally {
			//System.out.println("Deconnecting reader...");
			
			try {
				socket.close();
			} catch (Exception e) {
				GUI.showError("Erreur lors de la deconnexion du lecteur des messages recus.");
			}
			//System.out.println("Reader deconnected");
		}

	}

	/**
	 * Retourne le groupe qui utilise ce SocketReader
	 * @return Le groupe qui utilise ce SocketReader
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Permet de decoder un message a partir d'une chaine de caracteres
	 * @param stringData La chaine de caracteres
	 * @return Le Message decode
	 * @throws ClassNotFoundException Si une erreur survient
	 * @throws IOException Si une erreur survient
	 */
	private Message decodeMessageFromString(String stringData) throws ClassNotFoundException, IOException {

		byte[] data = Base64.getDecoder().decode(stringData);

		ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
		return (Message) iStream.readObject();
	
	}
	
	/**
	 * Permet de decoder un fichier a partir d'une chaine de caracteres et de l'enregistrer
	 * @param fileData La chaine de caracteres
	 * @param filePath Ou enregistrer le fichier sur le disque
	 * @throws IOException Si une erreur survient
	 */
	private void decodeAndSaveFileFromString(String fileData, Path filePath) throws IOException {
		
		byte[] data = Base64.getDecoder().decode(fileData);
		
		FileOutputStream imageOutputFile = new FileOutputStream(filePath.toString());

        imageOutputFile.write(data);
        imageOutputFile.close();
	}

}
