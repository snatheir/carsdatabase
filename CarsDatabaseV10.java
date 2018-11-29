/*
* The "CarsDatabaseV10" class.
* @author Sharif Natheir
* @version May 5, 2015
* Purpose: to maintain a database of the top 25 cars, 5 in each of 5 categories:
* compact SUV, hatchback, sedan, luxury, and full-size.
* Notes: go to File -> preferences... -> Java -> set Max Memory for JVM to
* 100mb. This is so that there is enough memory to load everything, especially
* the images and audio.
* Known Bugs:
*/
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
// import java.io.IOException;
// import java.io.FileReader;
// import java.io.BufferedReader;
// import java.io.FileNotFoundException;
// import java.io.File;
// import java.io.Writer;
// import java.io.OutputStreamWriter;
// import java.io.BufferedWriter;
// import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
// import java.util.Calendar;
import java.text.DateFormat;
import java.awt.image.BufferedImage;

public class CarsDatabaseV10 extends Applet implements MouseListener, KeyListener
{
    // Place instance variables here
    //Wallpapers
    Image wp1, wp2, wp3, wp4, wp5, wp6, wp7, wp8, wp9, wp10, wp11;
    //Car images, in order of ranking.
    Image img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12, img13, img14, img15, img16, img17, img18, img19, img20, img21, img22, img23, img24, img25;

    //Global fonts
    Font titleFont = new Font ("Forte", 0, 120);
    Font backFont = new Font ("AR JULIAN", 0, 20);
    Font carNameFont = new Font ("Britannic Bold", 0, 30);
    Font subtitleFont = new Font ("MV Boli", 0, 50);
    Font inputCarFont = new Font ("Britannic Bold", 0, 50);
    Font editFont = new Font ("MV Boli", 0, 25);
    Font nameSignatureFont = new Font ("Edwardian Script ITC", 0, 80);
    Font mainMenuFont = new Font ("Britannic Bold", 0, 80);
    Font carListFont = new Font ("Britannic Bold", 0, 40);
    Font bodyTypeFont = new Font ("AR BLANCA", 0, 40);
    Font inputButtonFont = new Font ("AR BLANCA", 0, 19);

    //mute/unmute buttons
    Image mute, unmute;

    //Mouse coordinates
    int mx, my;

    //Time of editting.
    //http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html#iso8601timezone
    DateFormat df = new SimpleDateFormat ("dd/MM/yyyy 'at' HH:mm:ss z");

    // Get the date today using Calendar object.
    String reportDate = ("");

    //Global booleans for screen displaying.
    boolean mainMenu = true; //the main menu screen
    boolean allCars = false; // the all cars screen
    boolean hallOfFame = false; //hall of fame screen
    boolean pictures = false; //the pictures screen
    boolean top5Pictures = false; //luxury car screen
    boolean top10Pictures = false; //sedan car screen
    boolean top15Pictures = false; //full size car screen
    boolean top20Pictures = false; //compact SUV screen
    boolean top25Pictures = false; //hatchback screen
    boolean inputCar = false; //the input car screen
    boolean history = false; //the history screen
    boolean muteAudio = false; //controls music
    boolean carExists = false; //used to edit existing cars info

    //Used to read keyboard events
    //http://www.javakode.com/applets/05-keyboardInput/
    String userName = ("");
    String nameOfCar = ("");
    String ranking = ("");
    String country = ("");
    String bodyType = ("");
    String MSRP = ("");
    String gas = ("");
    int numOfEnters = 0;

    //Update instance variables, to update the graphics.
    private Image dbImage;
    private Graphics dbg;

    //To refer to the sounds.
    //http://www.tutorialspoint.com/javaexamples/applet_sound.htm
    AudioClip music, clickAudio;

    //Array of car names in order of ranking.
    String[] carName = new String [25];

    //Temporary car ranking to change the car name array.
    int newRanking = 0;
    int originalRanking = 0;

    //File reader
    //http://stackoverflow.com/questions/28003137/declaring-a-static-filewriter-and-filereader
    //Reads the text files and outputs them to the system.
    //@param carName the name of the car whose information is to be read
    public static void readFile (String carName)
    {
	String line = null;

	try
	{
	    //making a FileReader and wrapping it in a BufferedReader
	    FileReader fileReader = new FileReader (carName + ".txt");
	    BufferedReader bufferedReader = new BufferedReader (fileReader);
	    while ((line = bufferedReader.readLine ()) != null)
	    {
		//printing everything in the text file to the System.
		System.out.println (line);
	    }
	    //seperates each car's info.
	    System.out.println ("------------------------------------------------------------------------------");
	    bufferedReader.close ();
	}
	catch (final IOException e)
	{
	    System.out.println ("Error Loading File '" + carName + ".txt" + "'");
	}
    }


    //Picture maker for cars that do not have a picture.
    //http://www.tutorialspoint.com/java_dip/java_buffered_image.htm
    //@param nameOfCar the name of the car whose image is not in database.
    //@param ranking the ranking of the car which is to be drawn.
    public Image createErrorImage ()
    {
	BufferedImage bufferedImage = new BufferedImage (300, 200, BufferedImage.TYPE_INT_RGB);
	Graphics g = bufferedImage.getGraphics ();
	g.setFont (carNameFont);
	g.setColor (Color.RED);
	g.drawString ("CAR PIC NOT FOUND", 17, 110);
	return bufferedImage;
    }


    //Image drawer
    //loads images and draws them on the screen.
    //@param g the graphics console to be drawn on.
    //@param ranking the ranking of the car to be drawn.
    //@param x the x coordinate of the top left hand side.
    //@param y the y coordinate of the top left hand side.
    public void drawPicture (Graphics g, int ranking, int x, int y)
    {
	//"image not found" message put under all images.
	Image error = createErrorImage ();
	g.drawImage (error, x, y, this);

	//Checking if the car's rank has been changed.
	String line = null;
	int count = 0;
	String originalCar = ("");
	//String newCar = ("");
	boolean foundImage = false;
	int newRanking = 0;
	try
	{
	    //making a FileReader and wrapping it in a BufferedReader
	    FileReader fileReader = new FileReader ("originalCarNames.txt");
	    BufferedReader bufferedReader = new BufferedReader (fileReader);
	    while ((line = bufferedReader.readLine ()) != null)
	    {
		originalCar = line;
		count++;
		//finds the rank of the new car in the original car array.
		if ((carName [ranking - 1].trim ()).indexOf (originalCar.trim ()) >= 0)
		{
		    foundImage = true;
		    newRanking = count;
		}
	    }
	    bufferedReader.close ();
	}
	catch (final IOException e)
	{ /*ignore*/
	}

	//Toolkiting the Images if they're in database
	if (foundImage)
	{
	    Image picture = getToolkit ().getImage ("img" + newRanking + ".jpg");
	    g.drawImage (picture, x, y, null);
	}
    }


    //File writer
    //http://stackoverflow.com/questions/2885173/java-how-to-create-a-file-and-write-to-a-file
    //http://examples.javacodegeeks.com/core-java/io/how-to-create-file-in-java-example/
    //Writes to text files.
    //@param nameOfCar the name of the car being added.
    //@param ranking the ranking of the car being added (1-25).
    //@param country the country of manufacturing of the car.
    //@param bodyType the body type of the car being added.
    //@param MSRP the Manufacturer's Suggested Retail Price of the car being added.
    //@param gas the gas rating of the car.
    public void writeToFile (String nameOfCar, String country, String bodyType, String MSRP, String gas)
    {
	//getting rid of extra spaces.
	nameOfCar = nameOfCar.trim ();
	//Introducing the file
	File file = new File (nameOfCar + ".txt");
	for (int x = 0 ; x < 25 ; x++)
	{
	    if ((carName [x].trim ()).indexOf (nameOfCar) >= 0)
	    {
		carExists = true;
		originalRanking = x + 1;
	    }
	}
	if (!carExists) //skips making a file if car is in database already so that it can just edit the one that already exists.
	    try
	    {
		//create a new file if it doesn't exist already
		file.createNewFile ();
	    }

	catch (IOException e)
	{ /*ignore*/
	}
	Writer writer = null;
	try
	{
	    //writing all the information of the car to the text file.
	    System.getProperty ("line.separator"); //to seperate lines in the text file.
	    writer = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (file, false), "utf-8"));
	    writer.write (nameOfCar + "\r\n\r\n");
	    writer.write ("Country of Manufacturing: " + country.trim () + "\r\n\r\n");
	    writer.write ("Body Type: " + bodyType.trim () + "\r\n\r\n");
	    writer.write ("MSRP: $" + MSRP.trim () + "\r\n\r\n");
	    writer.write ("Gas Rating (mpg): " + gas.trim ());
	}
	catch (IOException e)
	{ /*ignore*/
	}
	finally
	{
	    try
	    {
		//closing the writer.
		writer.flush ();
		writer.close ();
	    }
	    catch (Exception e)
	    { /*ignore*/
	    }
	}
    }


    //History file writer.
    //Writes to the "history.txt" file.
    //@param userName the name of the contributor.
    //@param nameOfCar the name of the car the user added.
    //@param date the date at which the edit was made.
    public void writeToHistory (String userName, String nameOfCar, String date)
    {
	//Introducing the file
	File file = new File ("History.txt");
	Writer writer = null;
	try
	{
	    System.getProperty ("line.separator"); //to seperate lines in the text file.
	    writer = new BufferedWriter (new OutputStreamWriter (new FileOutputStream ("history.txt", true), "utf-8"));
	    writer.write (userName.trim () + "\r\n");
	    writer.write (nameOfCar.trim () + "\r\n");
	    writer.write (date.trim () + "\r\n");
	}
	catch (IOException e)
	{ /*ignore*/
	}
	finally
	{
	    try
	    {
		writer.flush ();
		writer.close ();
	    }
	    catch (Exception e)
	    { /*ignore*/
	    }
	}
    }


    //initialization method.
    public void init ()
    {
	//Reads the carNames text file and stores info in carName array
	String line = null;
	int count = 0;
	try
	{
	    //making a FileReader and wrapping it in a BufferedReader
	    FileReader fileReader = new FileReader ("carNames.txt");
	    BufferedReader bufferedReader = new BufferedReader (fileReader);
	    while ((line = bufferedReader.readLine ()) != null)
	    {
		carName [count] = line;
		count++;
	    }
	    bufferedReader.close ();
	}
	catch (final IOException e)
	{
	    System.out.println ("Error Loading File 'carNames.txt'");
	}

	//Toolkiting the Images

	//mute/unmute buttons
	mute = getToolkit ().getImage ("mute.png");
	unmute = getToolkit ().getImage ("unmute.png");

	//wallpapers
	wp1 = getToolkit ().getImage ("wp1.jpg");
	wp2 = getToolkit ().getImage ("wp2.jpg");
	wp3 = getToolkit ().getImage ("wp3.jpg");
	wp4 = getToolkit ().getImage ("wp4.jpg");
	wp5 = getToolkit ().getImage ("wp5.jpg");
	wp6 = getToolkit ().getImage ("wp6.jpg");
	wp7 = getToolkit ().getImage ("wp7.jpg");
	wp8 = getToolkit ().getImage ("wp8.jpg");
	wp9 = getToolkit ().getImage ("wp9.jpg");
	wp10 = getToolkit ().getImage ("wp10.jpg");
	wp11 = getToolkit ().getImage ("wp11.jpg");

	//This loads the sounds from the server in the same way as an Image.
	music = getAudioClip (getDocumentBase (), "music.wav");
	clickAudio = getAudioClip (getDocumentBase (), "click.wav");

	//Sets the size of the program.
	setSize (1280, 720);

	//Initializing the implements of the applet.
	addMouseListener (this);
	addKeyListener (this);

	//Play the first sound to let the user know the applet is loaded.
	music.play ();
    } // init method


    //Keyboard Events

    //When the user types in something.
    //@param KeyEvent 'e' recieves the input from the user.
    public void keyTyped (KeyEvent e)
    {
	char c = e.getKeyChar ();
	if (c != KeyEvent.CHAR_UNDEFINED && inputCar && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_ENTER)
	{
	    //as long as the key inputted is not undefined, the enter key, or the back space key
	    //determines which field to type in based on how many times the user hit the "enter" key.
	    if (numOfEnters == 0 && userName.length () < 20)
	    {
		userName += c; //adds the inputted key to the user's name
	    }
	    if (numOfEnters == 1 && nameOfCar.length () < 20)
	    {
		nameOfCar += c; //adds the inputted key to the inputted car's name
	    }
	    if (numOfEnters == 2 && ranking.length () < 2 && (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9'))
	    {
		ranking += c; //adds the inputted key to the car's ranking
	    }
	    if (numOfEnters == 3 && country.length () < 20)
	    {
		country += c; //adds the inputted key to the country of manufacturing
	    }
	    if (numOfEnters == 5 && MSRP.length () < 10 && (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '.'))
	    {
		MSRP += c; //adds the inputted key to the MSRP
	    }
	    if (numOfEnters == 6 && gas.length () < 2 && (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '.'))
	    {
		gas += c; //adds the inputted key to the gas rating.
	    }
	    //finishes the key typed event and repaints the screen.
	    e.consume ();
	    repaint ();
	}
    }


    //When the user presses a key.
    //@param KeyEvent 'e' recieves the input from the user.
    public void keyPressed (KeyEvent e)
    {
	//checks if its the Enter key
	int key = e.getKeyCode ();
	//Ensures that the user can't go on to next text field without filling out the current one.
	if (key == KeyEvent.VK_ENTER && ((numOfEnters == 0 && userName.length () >= 1) || (numOfEnters == 1 && nameOfCar.length () >= 1) || (numOfEnters == 2 && Integer.parseInt (ranking) > 0 && Integer.parseInt (ranking) <= 25) || (numOfEnters == 3 && country.length () >= 1) || (numOfEnters == 5 && MSRP.length () >= 1) || (numOfEnters == 6 && gas.length () >= 1)))
	{
	    numOfEnters++;
	}
	//back space key to allow erasing of accidentally inputted car info.
	if (key == KeyEvent.VK_BACK_SPACE)
	{
	    //erases the last letter of the answers to the text field by using substrings.
	    if (numOfEnters == 0 && userName.length () > 0)
	    {
		userName = userName.substring (0, userName.length () - 1);
	    }
	    if (numOfEnters == 1 && nameOfCar.length () > 0)
	    {
		nameOfCar = nameOfCar.substring (0, nameOfCar.length () - 1);
	    }
	    if (numOfEnters == 2 && ranking.length () > 0)
	    {
		ranking = ranking.substring (0, ranking.length () - 1);
	    }
	    if (numOfEnters == 3 && country.length () > 0)
	    {
		country = country.substring (0, country.length () - 1);
	    }
	    if (numOfEnters == 4 && bodyType.length () > 0)
	    {
		bodyType = bodyType.substring (0, bodyType.length () - 1);
	    }
	    if (numOfEnters == 5 && MSRP.length () > 0)
	    {
		MSRP = MSRP.substring (0, MSRP.length () - 1);
	    }
	    if (numOfEnters == 6 && gas.length () > 0)
	    {
		gas = gas.substring (0, gas.length () - 1);
	    }
	}
	e.consume ();
    }


    //needed by KeyListener to operate.
    public void keyReleased (KeyEvent e)
    {
    }


    //Mouse Events

    //When the mouse button is pressed only.
    //@param 'e' recieves the input from the user.
    public void mousePressed (MouseEvent e)
    {
	//Called after a mouse button is clicked.
	mx = e.getX ();
	my = e.getY ();

	if (mainMenu) //main menu
	{
	    if (mx >= 430 && mx <= 780 && my >= 190 && my <= 255) //all cars button
	    {
		mainMenu = false;
		allCars = true;
	    }
	    else if (mx >= 340 && mx <= 860 && my >= 270 && my <= 335) //hall of fame button
	    {
		mainMenu = false;
		hallOfFame = true;
	    }
	    else if (mx >= 405 && mx <= 785 && my >= 345 && my <= 420) //pictures button
	    {
		mainMenu = false;
		pictures = true;
	    }
	    else if (mx >= 390 && mx <= 790 && my >= 430 && my <= 495) //input car button
	    {
		mainMenu = false;
		inputCar = true;
	    }
	    else if (mx >= 425 && mx <= 755 && my >= 510 && my <= 575) //history button
	    {
		mainMenu = false;
		history = true;
	    }
	    //https://docs.oracle.com/javase/7/docs/api/java/lang/System.html#exit(int)
	    else if (mx >= 500 && mx <= 680 && my >= 590 && my <= 655) //exit button
		System.exit (0);
	}

	else if (allCars) //all cars
	{
	    if (mx >= 5 && mx <= 405 && my >= 135 && my <= 190) //car rank 1
		readFile (carName [0]);
	    else if (mx >= 5 && mx <= 405 && my >= 195 && my <= 250) //car rank 2
		readFile (carName [1]);
	    else if (mx >= 5 && mx <= 405 && my >= 255 && my <= 310) //car rank 3
		readFile (carName [2]);
	    else if (mx >= 5 && mx <= 405 && my >= 315 && my <= 370) //car rank 4
		readFile (carName [3]);
	    else if (mx >= 5 && mx <= 405 && my >= 375 && my <= 430) //car rank 5
		readFile (carName [4]);
	    else if (mx >= 5 && mx <= 405 && my >= 435 && my <= 490) //car rank 6
		readFile (carName [5]);
	    else if (mx >= 5 && mx <= 405 && my >= 495 && my <= 550) //car rank 7
		readFile (carName [6]);
	    else if (mx >= 5 && mx <= 405 && my >= 555 && my <= 610) //car rank 8
		readFile (carName [7]);
	    else if (mx >= 5 && mx <= 405 && my >= 615 && my <= 670) //car rank 9
		readFile (carName [8]);
	    else if (mx >= 440 && mx <= 840 && my >= 135 && my <= 190) //car rank 10
		readFile (carName [9]);
	    else if (mx >= 440 && mx <= 840 && my >= 195 && my <= 250) //car rank 11
		readFile (carName [10]);
	    else if (mx >= 440 && mx <= 840 && my >= 255 && my <= 310) //car rank 12
		readFile (carName [11]);
	    else if (mx >= 440 && mx <= 840 && my >= 315 && my <= 370) //car rank 13
		readFile (carName [12]);
	    else if (mx >= 440 && mx <= 840 && my >= 375 && my <= 430) //car rank 14
		readFile (carName [13]);
	    else if (mx >= 440 && mx <= 840 && my >= 435 && my <= 490) //car rank 15
		readFile (carName [14]);
	    else if (mx >= 440 && mx <= 840 && my >= 495 && my <= 550) //car rank 16
		readFile (carName [15]);
	    else if (mx >= 440 && mx <= 840 && my >= 555 && my <= 610) //car rank 17
		readFile (carName [16]);
	    else if (mx >= 440 && mx <= 840 && my >= 615 && my <= 670) //car rank 18
		readFile (carName [17]);
	    else if (mx >= 875 && mx <= 1275 && my >= 135 && my <= 190) //car rank 19
		readFile (carName [18]);
	    else if (mx >= 875 && mx <= 1275 && my >= 195 && my <= 250) //car rank 20
		readFile (carName [19]);
	    else if (mx >= 875 && mx <= 1275 && my >= 255 && my <= 310) //car rank 21
		readFile (carName [20]);
	    else if (mx >= 875 && mx <= 1275 && my >= 315 && my <= 370) //car rank 22
		readFile (carName [21]);
	    else if (mx >= 875 && mx <= 1275 && my >= 375 && my <= 430) //car rank 23
		readFile (carName [22]);
	    else if (mx >= 875 && mx <= 1275 && my >= 435 && my <= 490) //car rank 24
		readFile (carName [23]);
	    else if (mx >= 875 && mx <= 1275 && my >= 495 && my <= 550) //car rank 25
		readFile (carName [24]);
	    else if (mx >= 1220 && mx <= 1270 && my >= 5 && my <= 20) //back button
	    {
		allCars = false;
		mainMenu = true;
	    }
	}


	else if (hallOfFame) //hall of fame
	{
	    if (mx >= 485 && mx <= 795 && my >= 85 && my <= 295) //car rank 1
		readFile (carName [0]);
	    else if (mx >= 275 && mx <= 585 && my >= 295 && my <= 505) //car rank 2
		readFile (carName [1]);
	    else if (mx >= 695 && mx <= 1005 && my >= 295 && my <= 505) //car rank 3
		readFile (carName [2]);
	    else if (mx >= 145 && mx <= 455 && my >= 505 && my <= 715) //car rank 4
		readFile (carName [3]);
	    else if (mx >= 825 && mx <= 1135 && my >= 505 && my <= 715) //car rank 5
		readFile (carName [4]);
	    else if (mx >= 1220 && mx <= 1270 && my >= 5 && my <= 20) //back button
	    {
		hallOfFame = false;
		mainMenu = true;
	    }

	}


	else if (pictures) //pictures of cars by ranking
	{
	    if (mx >= 25 && mx <= 335 && my >= 120 && my <= 330) //top 5
	    {
		pictures = false;
		top5Pictures = true;
	    }
	    else if (mx >= 225 && mx <= 535 && my >= 380 && my <= 590) //6-10
	    {
		pictures = false;
		top10Pictures = true;
	    }
	    else if (mx >= 485 && mx <= 795 && my >= 120 && my <= 330) //11-15
	    {
		pictures = false;
		top15Pictures = true;
	    }
	    else if (mx >= 745 && mx <= 1055 && my >= 380 && my <= 590) //16-20
	    {
		pictures = false;
		top20Pictures = true;
	    }
	    else if (mx >= 945 && mx <= 1255 && my >= 120 && my <= 330) //21-25
	    {
		pictures = false;
		top25Pictures = true;
	    }
	    else if (mx >= 1220 && mx <= 1270 && my >= 5 && my <= 20) //back button
	    {
		pictures = false;
		mainMenu = true;
	    }
	}


	else if (top5Pictures) //top 5 pictures screen
	{

	    if (mx >= 25 && mx <= 335 && my >= 120 && my <= 330) //car rank 1
		readFile (carName [0]);
	    else if (mx >= 225 && mx <= 535 && my >= 380 && my <= 590) //car rank 2
		readFile (carName [1]);
	    else if (mx >= 485 && mx <= 795 && my >= 120 && my <= 330) //car rank 3
		readFile (carName [2]);
	    else if (mx >= 745 && mx <= 1055 && my >= 380 && my <= 590) //car rank 4
		readFile (carName [3]);
	    else if (mx >= 945 && mx <= 1255 && my >= 120 && my <= 330) //car rank 5
		readFile (carName [4]);
	    else if (mx >= 1220 && mx <= 1270 && my >= 5 && my <= 20) //back button
	    {
		top5Pictures = false;
		pictures = true;
	    }
	}


	else if (top10Pictures) //6-10 pictures screen
	{

	    if (mx >= 25 && mx <= 335 && my >= 120 && my <= 330) //car rank 6
		readFile (carName [5]);
	    else if (mx >= 225 && mx <= 535 && my >= 380 && my <= 590) //car rank 7
		readFile (carName [6]);
	    else if (mx >= 485 && mx <= 795 && my >= 120 && my <= 330) //car rank 8
		readFile (carName [7]);
	    else if (mx >= 745 && mx <= 1055 && my >= 380 && my <= 590) //car rank 9
		readFile (carName [8]);
	    else if (mx >= 945 && mx <= 1255 && my >= 120 && my <= 330) //car rank 10
		readFile (carName [9]);
	    else if (mx >= 1220 && mx <= 1270 && my >= 5 && my <= 20) //back button
	    {
		top10Pictures = false;
		pictures = true;
	    }
	}


	else if (top15Pictures) //11-15 pictures screen
	{
	    if (mx >= 25 && mx <= 335 && my >= 120 && my <= 330) //car rank 11
		readFile (carName [10]);
	    else if (mx >= 225 && mx <= 535 && my >= 380 && my <= 590) //car rank 12
		readFile (carName [11]);
	    else if (mx >= 485 && mx <= 795 && my >= 120 && my <= 330) //car rank 13
		readFile (carName [12]);
	    else if (mx >= 745 && mx <= 1055 && my >= 380 && my <= 590) //car rank 14
		readFile (carName [13]);
	    else if (mx >= 945 && mx <= 1255 && my >= 120 && my <= 330) //car rank 15
		readFile (carName [14]);
	    else if (mx >= 1220 && mx <= 1270 && my >= 5 && my <= 20) //back button
	    {
		top15Pictures = false;
		pictures = true;
	    }
	}


	else if (top20Pictures) //16-20 pictures screen
	{
	    if (mx >= 25 && mx <= 335 && my >= 120 && my <= 330) //car rank 16
		readFile (carName [15]);
	    else if (mx >= 225 && mx <= 535 && my >= 380 && my <= 590) //car rank 17
		readFile (carName [16]);
	    else if (mx >= 485 && mx <= 795 && my >= 120 && my <= 330) //car rank 18
		readFile (carName [17]);
	    else if (mx >= 745 && mx <= 1055 && my >= 380 && my <= 590) //car rank 19
		readFile (carName [18]);
	    else if (mx >= 945 && mx <= 1255 && my >= 120 && my <= 330) //car rank 20
		readFile (carName [19]);
	    else if (mx >= 1220 && mx <= 1270 && my >= 5 && my <= 20) //back button
	    {
		top20Pictures = false;
		pictures = true;
	    }
	}

	else if (top25Pictures) //21-25 pictures screen
	{
	    if (mx >= 25 && mx <= 335 && my >= 120 && my <= 330) //car rank 21
		readFile (carName [20]);
	    else if (mx >= 225 && mx <= 535 && my >= 380 && my <= 590) //car rank 22
		readFile (carName [21]);
	    else if (mx >= 485 && mx <= 795 && my >= 120 && my <= 330) //car rank 23
		readFile (carName [22]);
	    else if (mx >= 745 && mx <= 1055 && my >= 380 && my <= 590) //car rank 24
		readFile (carName [23]);
	    else if (mx >= 945 && mx <= 1255 && my >= 120 && my <= 330) //car rank 25
		readFile (carName [24]);
	    else if (mx >= 1220 && mx <= 1270 && my >= 5 && my <= 20) //back button
	    {
		top25Pictures = false;
		pictures = true;
	    }
	}

	else if (inputCar) //input car screen
	{
	    if (mx >= 1220 && mx <= 1270 && my >= 5 && my <= 20) //back button
	    {
		//resetting keyboard variables
		userName = ("");
		nameOfCar = ("");
		ranking = ("");
		country = ("");
		bodyType = ("");
		MSRP = ("");
		gas = ("");
		numOfEnters = 0;
		inputCar = false;
		mainMenu = true;
	    }

	    //Mouse pressed buttons for body type (border)
	    else if (mx >= 580 && mx <= 710 && my >= 415 && my <= 445 && numOfEnters == 4) //luxury button
	    {
		bodyType = ("luxury");
		numOfEnters = 5;
	    }
	    else if (mx >= 720 && mx <= 850 && my >= 415 && my <= 445 && numOfEnters == 4) //sedan button
	    {
		bodyType = ("sedan");
		numOfEnters = 5;
	    }
	    else if (mx >= 860 && mx <= 990 && my >= 415 && my <= 445 && numOfEnters == 4) //full-size button
	    {
		bodyType = ("full-size");
		numOfEnters = 5;
	    }
	    else if (mx >= 1000 && mx <= 1130 && my >= 415 && my <= 445 && numOfEnters == 4) //compact SUV button
	    {
		bodyType = ("compact SUV");
		numOfEnters = 5;
	    }
	    else if (mx >= 1140 && mx <= 1270 && my >= 415 && my <= 445 && numOfEnters == 4) //hatchback button
	    {
		bodyType = ("hatchback");
		numOfEnters = 5;
	    }

	    else if (mx >= 415 && mx <= 795 && my >= 595 && my <= 670 && numOfEnters >= 6) //submit button
	    {
		//writing everything to the file.
		writeToFile (nameOfCar, country, bodyType, MSRP, gas);
		// Using DateFormat format method we can create a string representation of a date with the defined format.
		Date timeNow = Calendar.getInstance ().getTime ();
		timeNow.getTime ();
		reportDate = df.format (timeNow);
		writeToHistory (userName, nameOfCar, reportDate);
		//Storing temporary ranking variable for name array.
		//http://stackoverflow.com/questions/5585779/converting-string-to-int-in-java
		newRanking = Integer.parseInt (ranking);

		//updating car name array if car is not in database already.
		String[] carNameTemp = new String [25];
		for (int x = 0 ; x < carName.length ; x++)
		{
		    carNameTemp [x] = carName [x];
		}
		if (!carExists)
		{
		    for (int x = 24 ; x >= newRanking ; x--)
		    {
			carName [x] = carNameTemp [x - 1];
		    }
		    carName [newRanking - 1] = nameOfCar; //puts the users car in name array.
		}
		else if (carExists)
		{
		    //if the car goes up in rank (closer to rank 1)
		    if (newRanking < originalRanking)
		    {
			for (int x = newRanking - 1 ; x <= originalRanking - 2 ; x++)
			    carName [x + 1] = carNameTemp [x];
			carName [newRanking - 1] = carNameTemp [originalRanking - 1];
		    }

		    //if the car goes down in rank (closer to rank 25)
		    if (newRanking > originalRanking)
		    {
			for (int x = originalRanking - 1 ; x <= newRanking - 2 ; x++)
			    carName [x] = carNameTemp [x + 1];
			carName [newRanking - 1] = carNameTemp [originalRanking - 1];
		    }
		}
		//resetting keyboard variables
		userName = ("");
		nameOfCar = ("");
		ranking = ("");
		country = ("");
		bodyType = ("");
		MSRP = ("");
		gas = ("");
		reportDate = ("");
		numOfEnters = 0;
		inputCar = false;
		mainMenu = true;
		//Updating carNames text with the new car.
		//Introducing the file
		File file = new File ("carNames.txt");
		Writer writer = null;
		try
		{
		    System.getProperty ("line.separator"); //to seperate lines in the text file.
		    writer = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (file, false), "utf-8"));
		    for (int x = 0 ; x < 25 ; x++)
			writer.write (carName [x].trim () + "\r\n");
		}
		catch (IOException ex)
		{ /*ignore*/
		}
		finally
		{
		    try
		    {
			writer.flush ();
			writer.close ();
		    }
		    catch (Exception ex)
		    { /*ignore*/
		    }
		}
	    }
	}
	else if (history)
	{
	    if (mx >= 1220 && mx <= 1270 && my >= 5 && my <= 20) //back button
	    {
		history = false;
		mainMenu = true;
	    }
	}

	//mute/unmute button
	if (mx >= 1230 && mx <= 1280 && my >= 670 && my <= 720)
	{
	    if (muteAudio == true)
	    {
		muteAudio = false;
		music.play ();
	    }
	    else if (muteAudio == false)
	    {
		muteAudio = true;
		music.stop ();
	    }
	}

	//repaints g everytime the user clicks to make sure they are on the right page.
	clickAudio.play (); //plays a sound to let the user know that their click was received.
	repaint ();
    }


    //Needed by MouseListener to operate.
    public void mouseEntered (MouseEvent e)
    {
    }


    public void mouseExited (MouseEvent e)
    {
    }


    public void mouseClicked (MouseEvent e)
    {
    }


    public void mouseReleased (MouseEvent e)
    {
    }


    //Update Method implements double buffering.
    //@param 'g' where the graphics are drawn.
    public void update (Graphics g)
    {
	//Initialize buffer
	if (dbImage == null)
	{
	    dbImage = createImage (this.getSize ().width, this.getSize ().height);
	    dbg = dbImage.getGraphics ();
	}
	//Clear screen in background
	dbg.setColor (getBackground ());
	dbg.fillRect (0, 0, this.getSize ().width, this.getSize ().height);
	//Draw elements in background
	dbg.setColor (getForeground ());
	paint (dbg);
	//Draw image on the screen
	g.drawImage (dbImage, 0, 0, this);
    }


    //actually draws everything to graphics console g.
    public void paint (Graphics g)
    {
	if (mainMenu) //main menu screen
	{
	    //Main Menu Background
	    g.drawImage (wp1, 0, 0, null);
	    g.setFont (titleFont);
	    g.setColor (Color.CYAN);
	    g.drawString ("TOP CARS DATABASE", 30, 120);
	    g.setFont (subtitleFont);
	    g.setColor (Color.BLACK);
	    g.drawString ("PLEASE CLICK ON ONE OF THE BUTTONS:", 100, 180);
	    g.setFont (nameSignatureFont);
	    g.drawString ("Sharif Natheir", 900, 630);

	    //Menu option borders
	    g.fillRect (425, 185, 360, 75); //all cars
	    g.fillRect (335, 265, 530, 75); //hall of fame
	    g.fillRect (405, 345, 380, 75); //pictures
	    g.fillRect (385, 425, 410, 75); //input car
	    g.fillRect (420, 505, 340, 75); //history
	    g.fillRect (495, 585, 190, 75); //exit
	    //Menu options
	    g.setColor (Color.GRAY);
	    g.fillRect (430, 190, 350, 65); //all cars
	    g.fillRect (340, 270, 520, 65); //hall of fame
	    g.fillRect (410, 350, 370, 65); //pictures
	    g.fillRect (390, 430, 400, 65); //input car
	    g.fillRect (425, 510, 330, 65); //history
	    g.fillRect (500, 590, 180, 65); //exit

	    g.setColor (Color.GREEN);
	    g.setFont (mainMenuFont);
	    g.drawString ("ALL CARS", 440, 250);
	    g.drawString ("HALL OF FAME", 350, 330);
	    g.drawString ("PICTURES", 420, 410);
	    g.drawString ("INPUT CAR", 400, 490);
	    g.drawString ("HISTORY", 435, 570);
	    g.setColor (Color.RED);
	    g.drawString ("EXIT", 510, 650);
	}
	if (allCars) //all cars screen
	{
	    //All cars Background
	    g.drawImage (wp2, 0, 0, null);
	    //title
	    g.setFont (titleFont);
	    g.setColor (Color.CYAN);
	    g.drawString ("ALL CARS", 370, 80);
	    g.setFont (subtitleFont);
	    g.setColor (Color.WHITE);
	    g.drawString ("IN ORDER OF RANKING", 320, 130);
	    //List of cars
	    g.setFont (carListFont);
	    g.setColor (Color.RED);
	    //First row
	    g.drawString ("1. " + carName [0], 10, 180);
	    g.drawRect (5, 135, 400, 55);
	    g.drawString ("2. " + carName [1], 10, 240);
	    g.drawRect (5, 195, 400, 55);
	    g.drawString ("3. " + carName [2], 10, 300);
	    g.drawRect (5, 255, 400, 55);
	    g.drawString ("4. " + carName [3], 10, 360);
	    g.drawRect (5, 315, 400, 55);
	    g.drawString ("5. " + carName [4], 10, 420);
	    g.drawRect (5, 375, 400, 55);
	    g.drawString ("6. " + carName [5], 10, 480);
	    g.drawRect (5, 435, 400, 55);
	    g.drawString ("7. " + carName [6], 10, 540);
	    g.drawRect (5, 495, 400, 55);
	    g.drawString ("8. " + carName [7], 10, 600);
	    g.drawRect (5, 555, 400, 55);
	    g.drawString ("9. " + carName [8], 10, 660);
	    g.drawRect (5, 615, 400, 55);

	    //second row
	    g.drawString ("10. " + carName [9], 450, 180);
	    g.drawRect (440, 135, 400, 55);
	    g.drawString ("11. " + carName [10], 450, 240);
	    g.drawRect (440, 195, 400, 55);
	    g.drawString ("12. " + carName [11], 450, 300);
	    g.drawRect (440, 255, 400, 55);
	    g.drawString ("13. " + carName [12], 450, 360);
	    g.drawRect (440, 315, 400, 55);
	    g.drawString ("14. " + carName [13], 450, 420);
	    g.drawRect (440, 375, 400, 55);
	    g.drawString ("15. " + carName [14], 450, 480);
	    g.drawRect (440, 435, 400, 55);
	    g.drawString ("16. " + carName [15], 450, 540);
	    g.drawRect (440, 495, 400, 55);
	    g.drawString ("17. " + carName [16], 450, 600);
	    g.drawRect (440, 555, 400, 55);
	    g.drawString ("18. " + carName [17], 450, 660);
	    g.drawRect (440, 615, 400, 55);

	    //third row
	    g.drawString ("19. " + carName [18], 880, 180);
	    g.drawRect (875, 135, 400, 55);
	    g.drawString ("20. " + carName [19], 880, 240);
	    g.drawRect (875, 195, 400, 55);
	    g.drawString ("21. " + carName [20], 880, 300);
	    g.drawRect (875, 255, 400, 55);
	    g.drawString ("22. " + carName [21], 880, 360);
	    g.drawRect (875, 315, 400, 55);
	    g.drawString ("23. " + carName [22], 880, 420);
	    g.drawRect (875, 375, 400, 55);
	    g.drawString ("24. " + carName [23], 880, 480);
	    g.drawRect (875, 435, 400, 55);
	    g.drawString ("25. " + carName [24], 880, 540);
	    g.drawRect (875, 495, 400, 55);
	}
	if (hallOfFame) //hall of fame screen
	{
	    { //Hall of Fame Background
		g.drawImage (wp3, 0, 0, null);
		g.setFont (titleFont);
		g.setColor (Color.CYAN);
		g.drawString ("HALL OF FAME", 220, 80);
		//Picture borders and ranking labels.

		//car rank 1; default: Mercedes benz c
		g.setColor (Color.GREEN);
		g.fillRect (485, 85, 310, 210);
		g.drawString ("1", 620, 380);
		drawPicture (g, 1, 490, 90);
		g.setFont (carNameFont);
		g.drawString (carName [0], 490, 285);

		//car rank 2; default: Lexus RC
		g.setColor (Color.ORANGE);
		g.fillRect (275, 295, 310, 210);
		g.setFont (titleFont);
		g.drawString ("2", 200, 500);
		drawPicture (g, 2, 280, 300);
		g.setFont (carNameFont);
		g.drawString (carName [1], 280, 495);

		//car rank 3; default: BMW Z-series
		g.setColor (new Color (162, 17, 162));
		g.fillRect (695, 295, 310, 210);
		g.setFont (titleFont);
		g.drawString ("3", 1010, 500);
		drawPicture (g, 3, 700, 300);
		g.setFont (carNameFont);
		g.drawString (carName [2], 700, 495);

		//car rank 4; default: Audi A5
		g.setColor (Color.BLUE);
		g.fillRect (145, 505, 310, 210);
		g.setFont (titleFont);
		g.drawString ("4", 50, 650);
		drawPicture (g, 4, 150, 510);
		g.setFont (carNameFont);
		g.drawString (carName [3], 150, 705);

		//car rank 5; default: BMW 3-series
		g.setColor (Color.RED);
		g.fillRect (825, 505, 310, 210);
		g.setFont (titleFont);
		g.drawString ("5", 1150, 700);
		drawPicture (g, 5, 830, 510);
		g.setFont (carNameFont);
		g.drawString (carName [4], 830, 705);
	    }
	}
	if (pictures) //search by pictures screen
	{
	    {
		//Pictures Background
		g.drawImage (wp4, 0, 0, null);
		//Title
		g.setFont (titleFont);
		g.setColor (Color.CYAN);
		g.drawString ("SEARCH: BY PICTURE", 0, 80);

		//Picture borders and body type labels.
		g.setFont (bodyTypeFont);
		g.setColor (Color.CYAN);

		//top 5
		g.setColor (Color.GREEN);
		g.fillRect (25, 120, 310, 210);
		drawPicture (g, 1, 30, 125);
		g.drawString ("TOP 5", 35, 320);

		//6-10
		g.setColor (Color.ORANGE);
		g.fillRect (225, 380, 310, 210);
		drawPicture (g, 6, 230, 385);
		g.drawString ("6-10", 235, 580);

		//11-15
		g.setColor (new Color (162, 17, 162));
		g.fillRect (485, 120, 310, 210);
		drawPicture (g, 11, 490, 125);
		g.drawString ("11-15", 495, 320);

		//16-20
		g.setColor (Color.BLUE);
		g.fillRect (745, 380, 310, 210);
		drawPicture (g, 16, 750, 385);
		g.drawString ("16-20", 755, 580);

		//21-25
		g.setColor (Color.RED);
		g.fillRect (945, 120, 310, 210);
		drawPicture (g, 21, 950, 125);
		g.drawString ("21-25", 955, 320);
	    }
	}
	if (inputCar) //input a car screen
	{
	    //Car inputting Background
	    g.drawImage (wp5, 0, 0, null);
	    g.setColor (Color.BLACK);
	    g.setFont (subtitleFont);
	    g.drawString ("USE THE KEYBOARD & MOUSE TO INPUT DATA", 30, 120);
	    g.setFont (editFont);
	    g.drawString ("TO EDIT AN EXISTING CAR'S INFORMATION, SIMPLY TYPE ITS NAME IN THE 'CAR'S NAME' FIELD", 20, 160);

	    //Title
	    g.setFont (titleFont);
	    g.setColor (Color.CYAN);
	    g.drawString ("INPUT CAR", 330, 80);

	    //Mouse pressed buttons for searching(border)
	    g.setColor (Color.BLUE);
	    g.fillRect (580, 415, 130, 30); //top 5
	    g.fillRect (720, 415, 130, 30); //6-10
	    g.fillRect (860, 415, 130, 30); //11-15
	    g.fillRect (1000, 415, 130, 30); //16-20
	    g.fillRect (1140, 415, 130, 30); //21-25

	    //Mouse pressed buttons for searching (inner)
	    g.setColor (Color.YELLOW);
	    g.fillRect (583, 418, 124, 24); //top 5
	    g.fillRect (723, 418, 124, 24); //6-10
	    g.fillRect (863, 418, 124, 24); //11-15
	    g.fillRect (1003, 418, 124, 24); //16-20
	    g.fillRect (1143, 418, 124, 24); //21-25

	    //button labelling
	    g.setColor (Color.RED);
	    g.setFont (inputButtonFont);
	    g.drawString ("LUXURY", 610, 438);
	    g.drawString ("SEDAN", 753, 438);
	    g.drawString ("FULL-SIZE", 885, 438);
	    g.drawString ("COMPACT SUV", 1005, 438);
	    g.drawString ("HATCHBACK", 1153, 438);

	    //Submit button
	    g.setColor (Color.CYAN);
	    g.setFont (subtitleFont);
	    g.fillRect (415, 595, 380, 75); //submit button border
	    g.setColor (Color.RED);
	    g.fillRect (420, 600, 370, 65); //submit button
	    g.setColor (Color.BLACK);
	    g.drawString ("SUBMIT DATA", 430, 650);
	    g.setColor (Color.ORANGE);

	    //Menu form
	    g.setFont (inputCarFont);
	    g.drawString ("YOUR NAME: ", 10, 210);
	    g.drawString ("CAR'S NAME: ", 10, 270);
	    g.drawString ("RANKING (1-25): ", 10, 330);
	    g.drawString ("COUNTRY OF MANUFACTURING: ", 10, 390);
	    g.drawString ("BODY TYPE: ", 10, 450);
	    g.drawString ("MSRP: $", 10, 510);
	    g.drawString ("GAS RATING (MPG): ", 10, 570);

	    //Responses
	    g.setColor (Color.RED);
	    g.drawString (userName, 290, 210);
	    g.drawString (nameOfCar, 290, 270);
	    g.drawString (ranking, 385, 330);
	    g.drawString (country, 700, 390);
	    g.drawString (bodyType, 285, 450);
	    g.drawString (MSRP, 200, 510);
	    g.drawString (gas, 445, 570);

	    //keyboard selection rectangles, highlighting selected textbox
	    if (numOfEnters == 0)
		g.drawRect (5, 170, 1265, 45);
	    if (numOfEnters == 1)
		g.drawRect (5, 230, 1265, 45);
	    if (numOfEnters == 2)
		g.drawRect (5, 290, 1265, 45);
	    if (numOfEnters == 3)
		g.drawRect (5, 350, 1265, 45);
	    if (numOfEnters == 4)
		g.drawRect (5, 410, 1265, 45);
	    if (numOfEnters == 5)
		g.drawRect (5, 470, 1265, 45);
	    if (numOfEnters == 6)
		g.drawRect (5, 530, 1265, 45);
	    if (numOfEnters == 7)
		g.drawRect (405, 585, 400, 95);
	}
	if (history) //history screen
	{
	    //History Background
	    g.drawImage (wp6, 0, 0, null);
	    g.setFont (titleFont);
	    g.setColor (Color.CYAN);
	    g.drawString ("HISTORY OF EDITS", 110, 80);
	    g.setFont (inputCarFont);
	    g.setColor (Color.RED);
	    g.drawString ("CONTRIBUTER", 10, 180);
	    g.drawString ("CAR NAME", 450, 180);
	    g.drawString ("TIME", 800, 180);

	    //reading the history file for list of edits.
	    g.setFont (carNameFont);
	    String line = null;
	    int lineNum = 0;
	    String readHistory[] = new String [30];
	    for (int x = 0 ; x < 30 ; x++)
		readHistory [x] = " ";
	    try
	    {
		//making a FileReader and wrapping it in a BufferedReader
		FileReader fileReader = new FileReader ("History.txt");
		BufferedReader bufferedReader = new BufferedReader (fileReader);
		while ((line = bufferedReader.readLine ()) != null)
		{
		    //assigning values to each element of lineNum.
		    readHistory [lineNum % 30] = line;
		    lineNum++;
		}
		bufferedReader.close ();
	    }
	    catch (final IOException e)
	    {
		System.out.println ("Error Loading File 'History.txt'");
	    }
	    //painting the history
	    g.drawString (readHistory [0], 10, 280); //contributor1
	    g.drawString (readHistory [1], 450, 280); //car name
	    g.drawString (readHistory [2], 800, 280); //date
	    g.drawString (readHistory [3], 10, 320); //contributor2
	    g.drawString (readHistory [4], 450, 320); //car name
	    g.drawString (readHistory [5], 800, 320); //date
	    g.drawString (readHistory [6], 10, 360); //contributor3
	    g.drawString (readHistory [7], 450, 360); //car name
	    g.drawString (readHistory [8], 800, 360); //date
	    g.drawString (readHistory [9], 10, 400); //contributor4
	    g.drawString (readHistory [10], 450, 400); //car name
	    g.drawString (readHistory [11], 800, 400); //date
	    g.drawString (readHistory [12], 10, 440); //contributor5
	    g.drawString (readHistory [13], 450, 440); //car name
	    g.drawString (readHistory [14], 800, 440); //date
	    g.drawString (readHistory [15], 10, 480); //contributor6
	    g.drawString (readHistory [16], 450, 480); //car name
	    g.drawString (readHistory [17], 800, 480); //date
	    g.drawString (readHistory [18], 10, 520); //contributor7
	    g.drawString (readHistory [19], 450, 520); //car name
	    g.drawString (readHistory [20], 800, 520); //date
	    g.drawString (readHistory [21], 10, 560); //contributor8
	    g.drawString (readHistory [22], 450, 560); //car name
	    g.drawString (readHistory [23], 800, 560); //date
	    g.drawString (readHistory [24], 10, 600); //contributor9
	    g.drawString (readHistory [25], 450, 600); //car name
	    g.drawString (readHistory [26], 800, 600); //date
	    g.drawString (readHistory [27], 10, 640); //contributor10
	    g.drawString (readHistory [28], 450, 640); //car name
	    g.drawString (readHistory [29], 800, 640); //date
	}
	if (top5Pictures) //top 5 screen
	{
	    //top 5 Background
	    g.drawImage (wp7, 0, 0, null);
	    g.setFont (titleFont);
	    g.setColor (Color.CYAN);
	    g.drawString ("TOP 5 PICTURES", 200, 80);
	    //Picture borders and ranking labels.

	    //rank 1; default: Mercedes Benz C
	    g.setColor (Color.GREEN);
	    g.fillRect (25, 120, 310, 210);
	    drawPicture (g, 1, 30, 125);
	    g.setFont (carNameFont);
	    g.drawString (carName [0], 35, 320);

	    //rank 2; default: Lexus RC
	    g.setColor (Color.ORANGE);
	    g.fillRect (225, 380, 310, 210);
	    drawPicture (g, 2, 230, 385);
	    g.drawString (carName [1], 235, 580);

	    //rank 3; default: BMW Z-series
	    g.setColor (new Color (162, 17, 162));
	    g.fillRect (485, 120, 310, 210);
	    drawPicture (g, 3, 490, 125);
	    g.drawString (carName [2], 495, 320);

	    //rank 4; default: Audi A5
	    g.setColor (Color.BLUE);
	    g.fillRect (745, 380, 310, 210);
	    drawPicture (g, 4, 750, 385);
	    g.drawString (carName [3], 755, 580);

	    //rank 5; default: BMW 3-series
	    g.setColor (Color.RED);
	    g.fillRect (945, 120, 310, 210);
	    drawPicture (g, 5, 950, 125);
	    g.drawString (carName [4], 955, 320);
	}
	if (top10Pictures) //6-10 screen
	{
	    //top 10 background
	    g.drawImage (wp8, 0, 0, null);
	    g.setFont (titleFont);
	    g.setColor (Color.CYAN);
	    g.drawString ("6-10 PICTURES", 230, 80);
	    //Picture borders and ranking labels.

	    //rank 6; default: Honda Accord
	    g.setColor (Color.GREEN);
	    g.fillRect (25, 120, 310, 210);
	    drawPicture (g, 6, 30, 125);
	    g.setFont (carNameFont);
	    g.drawString (carName [5], 35, 320);

	    //rank 7; default: Mazda Mazda 6
	    g.setColor (Color.ORANGE);
	    g.fillRect (225, 380, 310, 210);
	    drawPicture (g, 7, 230, 385);
	    g.drawString (carName [6], 235, 580);

	    //rank 8; default: Volkswagen Passat
	    g.setColor (new Color (162, 17, 162));
	    g.fillRect (485, 120, 310, 210);
	    drawPicture (g, 8, 490, 125);
	    g.drawString (carName [7], 495, 320);

	    //rank 9; default: Toyota Camry
	    g.setColor (Color.BLUE);
	    g.fillRect (745, 380, 310, 210);
	    drawPicture (g, 9, 750, 385);
	    g.drawString (carName [8], 755, 580);

	    //rank 10; default: Toyota Corolla
	    g.setColor (Color.RED);
	    g.fillRect (945, 120, 310, 210);
	    drawPicture (g, 10, 950, 125);
	    g.drawString (carName [9], 955, 320);
	}
	if (top15Pictures) //11-15 screen
	{
	    //top 15 background
	    g.drawImage (wp9, 0, 0, null);
	    g.setFont (titleFont);
	    g.setColor (Color.CYAN);
	    g.drawString ("11-15 PICTURES", 200, 80);
	    //Picture borders and ranking labels.

	    //rank 11; default: Chevrolet Impala
	    g.setColor (Color.GREEN);
	    g.fillRect (25, 120, 310, 210);
	    drawPicture (g, 11, 30, 125);
	    g.setFont (carNameFont);
	    g.drawString (carName [10], 35, 320);

	    //rank 12; default: Toyota Avalon
	    g.setColor (Color.ORANGE);
	    g.fillRect (225, 380, 310, 210);
	    drawPicture (g, 12, 230, 385);
	    g.drawString (carName [11], 235, 580);

	    //rank 13; default: Buick LaCrosse
	    g.setColor (new Color (162, 17, 162));
	    g.fillRect (485, 120, 310, 210);
	    drawPicture (g, 13, 490, 125);
	    g.drawString (carName [12], 495, 320);

	    //rank 14; default: Ford Taurus
	    g.setColor (Color.BLUE);
	    g.fillRect (745, 380, 310, 210);
	    drawPicture (g, 14, 750, 385);
	    g.drawString (carName [13], 755, 580);

	    //rank 15; default: Dodge Charger
	    g.setColor (Color.RED);
	    g.fillRect (945, 120, 310, 210);
	    drawPicture (g, 15, 950, 125);
	    g.drawString (carName [14], 955, 320);
	}
	if (top20Pictures) //16-20 screen
	{
	    //top 20 pictures background
	    g.drawImage (wp10, 0, 0, null);
	    g.setFont (titleFont);
	    g.setColor (Color.CYAN);
	    g.drawString ("16-20 PICTURES", 200, 80);
	    //Picture borders and ranking labels.

	    //rank 16; default: Honda CR-V
	    g.setColor (Color.GREEN);
	    g.fillRect (25, 120, 310, 210);
	    drawPicture (g, 16, 30, 125);
	    g.setFont (carNameFont);
	    g.drawString (carName [15], 35, 320);

	    //rank 17; default: GMC Terrain
	    g.setColor (Color.ORANGE);
	    g.fillRect (225, 380, 310, 210);
	    drawPicture (g, 17, 230, 385);
	    g.drawString (carName [16], 235, 580);

	    //rank 18; default: Mazda CX-5
	    g.setColor (new Color (162, 17, 162));
	    g.fillRect (485, 120, 310, 210);
	    drawPicture (g, 18, 490, 125);
	    g.drawString (carName [17], 495, 320);

	    //rank 19; default: Nissan Rogue
	    g.setColor (Color.BLUE);
	    g.fillRect (745, 380, 310, 210);
	    drawPicture (g, 19, 750, 385);
	    g.drawString (carName [18], 755, 580);

	    //rank 20; default: Ford Escape
	    g.setColor (Color.RED);
	    g.fillRect (945, 120, 310, 210);
	    drawPicture (g, 20, 950, 125);
	    g.drawString (carName [19], 955, 320);
	}

	if (top25Pictures) //21-25 screen
	{
	    //top 25 background
	    g.drawImage (wp11, 0, 0, null);
	    g.setFont (titleFont);
	    g.setColor (Color.CYAN);
	    g.drawString ("21-25 PICTURES", 200, 80);
	    //Picture borders and ranking labels.

	    //rank 21; default: Honda Fit
	    g.setColor (Color.GREEN);
	    g.fillRect (25, 120, 310, 210);
	    drawPicture (g, 21, 30, 125);
	    g.setFont (carNameFont);
	    g.drawString (carName [20], 35, 320);

	    //rank 22; default: Volkswagen GTI
	    g.setColor (Color.ORANGE);
	    g.fillRect (225, 380, 310, 210);
	    drawPicture (g, 22, 230, 385);
	    g.drawString (carName [21], 235, 580);

	    //rank 23; default: Volkswagen Golf
	    g.setColor (new Color (162, 17, 162));
	    g.fillRect (485, 120, 310, 210);
	    drawPicture (g, 23, 490, 125);
	    g.drawString (carName [22], 495, 320);

	    //rank 24; default: Mazda Mazda 3
	    g.setColor (Color.BLUE);
	    g.fillRect (745, 380, 310, 210);
	    drawPicture (g, 24, 750, 385);
	    g.drawString (carName [23], 755, 580);

	    //rank 25; default: Kia Soul
	    g.setColor (Color.RED);
	    g.fillRect (945, 120, 310, 210);
	    drawPicture (g, 25, 950, 125);
	    g.drawString (carName [24], 955, 320);
	}

	//Draws sound button based on whether sound is enabled or disabled
	if (!muteAudio)
	    g.drawImage (mute, 1230, 670, null);
	else
	    g.drawImage (unmute, 1230, 670, null);

	//Back button
	if (!mainMenu) //every screen except main menu
	{
	    g.setColor (Color.BLACK);
	    g.fillRect (1217, 2, 56, 21);
	    g.setColor (Color.RED);
	    g.fillRect (1220, 5, 50, 15);
	    g.setColor (Color.BLACK);
	    g.setFont (backFont);
	    g.drawString ("BACK", 1220, 20);
	}
	//repaints g to show everything.
	repaint ();
	// Place the body of the drawing method here
    } // paint method
} // CarsDatabaseV10 class


