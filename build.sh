echo "Building JavaMessage bytecode..."
javac MessageClient.java
echo "Building JavaMessage archive..."
jar -cvfm MessageClient.jar ClientManifest MessageClient.class MessageSend.class MessageGUI.class MessageGet.class MessageField.class
echo "Trash collecting..."
rm MessageClient.class MessageSend.class MessageGUI.class MessageGet.class MessageField.class