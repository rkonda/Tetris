package game.tetris;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;


class Configuration {
	private Board board;
	List<int[][]> pieces;
	
	Configuration(Board board) {
		this.board = board;
		try {
			loadConfigurationFromFile();
		} catch(Exception exception) {
			System.out.println("Unable to read from configuration file.");
			loadDefaults();
		}
	}
	
	private void loadConfigurationFromFile() 
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException 
	{		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		File configFile = new File(board.configurationFilePath);
		if(!configFile.exists()) {
			configFile = new File("Config\\SampleConfig1.xml");
		}
		Document document = documentBuilder.parse(configFile);
		
		document.normalize();
		
		// Read HEIGHT
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression xpathExpression = xpath.compile("/Configuration/Board/@HEIGHT");
		NodeList heightNode = (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
		board.HEIGHT = Integer.parseInt( heightNode.item(0).getNodeValue() );

		// Read WIDTH
		xpathExpression = xpath.compile("/Configuration/Board/@WIDTH");
		NodeList widthNode = (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
		board.WIDTH = Integer.parseInt( widthNode.item(0).getNodeValue() );
		
		// Read all the pieces
		xpathExpression = xpath.compile("/Configuration/Pieces/Piece");
		
		NodeList pieceNodes = (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
		
		pieces = new ArrayList<int[][]>();
		for(int pieceIndex = 0; pieceIndex < pieceNodes.getLength(); pieceIndex++ ) {
			pieces.add( parsePiece( pieceNodes.item(pieceIndex) ) );
		}
	}
	
	public void loadPieces() {
		for( int[][] piece : pieces ) {
			board.pieces.add(piece);
		}
	}

	private int[][] parsePiece( Node pieceNode ) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression xpathExpression = xpath.compile("Location");
		NodeList locationNodeList = (NodeList)xpathExpression.evaluate(pieceNode, XPathConstants.NODESET);
		List<int[]> locations = new ArrayList<int[]>();
		int rowMax = Integer.MIN_VALUE, colMax = Integer.MIN_VALUE;
		int rowMin = Integer.MAX_VALUE, colMin = Integer.MAX_VALUE;
		for( int locationIndex = 0; locationIndex < locationNodeList.getLength(); locationIndex++ ) {
			NamedNodeMap attributesMap = locationNodeList.item(locationIndex).getAttributes();
			Node rowAttribute = attributesMap.getNamedItem("row");
			int row = Integer.parseInt( rowAttribute.getNodeValue() );
			Node colAttribute = attributesMap.getNamedItem("col");
			int col = Integer.parseInt( colAttribute.getNodeValue() );
			
			locations.add(new int[] { row, col } );
			
			if(row > rowMax) {
				rowMax = row;
			}
			if(row < rowMin) {
				rowMin = row;
			}
			if(col > colMax) {
				colMax = col;
			}
			if(col < colMin) {
				colMin = col;
			}
		}
		
		int pieceHeight = rowMax - rowMin + 1;
		int pieceWidth = colMax - colMin + 1;
		int[][] piece = new int[pieceHeight][pieceWidth];
		for(int[] location : locations ) {
			piece[location[0] - rowMin][location[1] - colMin] = 1;
		}
		
		return piece;
	}
	
	private void loadDefaults() {		
		board.HEIGHT = 20;
		board.WIDTH = 10;

		pieces = new ArrayList<int[][]>();
		
		int[][] rectangle = new int[1][4];
		rectangle[0][0] = 1;
		rectangle[0][1] = 1;
		rectangle[0][2] = 1;
		rectangle[0][3] = 1;

		pieces.add(rectangle);

		int[][] square = new int[2][2];
		square[0][0] = 1;
		square[0][1] = 1;
		square[1][0] = 1;
		square[1][1] = 1;

		pieces.add(square);

		int[][] snake = new int[2][3];
		snake[0][0] = 1;
		snake[0][1] = 1;
		snake[1][1] = 1;
		snake[1][2] = 1;

		pieces.add(snake);

		int[][] hammer = new int[2][3];
		hammer[0][1] = 1;
		hammer[1][0] = 1;
		hammer[1][1] = 1;
		hammer[1][2] = 1;

		pieces.add(hammer);
	}

}