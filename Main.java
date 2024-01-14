package code;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.time.*;
import java.util.Random;
import java.util.Scanner;

class Stadium {
    City city;
    String name;
    boolean booked = false;
    
    Stadium(City city, String name) {
        this.city = city;
        this.name = name;
    }
    
    Stadium() {
    }
}

class City {
    String name;
    ArrayList<Stadium> stadiums = new ArrayList<>();
    ArrayList<Team> teams = new ArrayList<>();
    int amountOfTeams;

    City(String name, int amountOfTeams) {
        this.name = name;
        this.amountOfTeams = amountOfTeams;
    }
    
    public int unbook() {
    	int count = 0;
    	for (Stadium stadium : stadiums) {
    		if (stadium.booked == true) {
    			count++;
    		}
    		stadium.booked = false;
    	}
    	return count;
    }
    
    public int book() {
    	int count = 0;
    	for (Stadium stadium : stadiums) {
    		if (stadium.booked == false) {
    			count++;
    		}
    		stadium.booked = true;
    	}
    	return count;
    }
    
    public int numStadiumsOpen() {
    	int count = 0;
    	for (Stadium stadium : stadiums) {
    		if (!stadium.booked) {
    			count++;
    		}
    	}
    	return count;
    }
}

class Game {
    Team homeTeam;
    Team awayTeam;
    Stadium stadium;
    City city;
    Boolean filler = false;

    Game(Team homeTeam, Team awayTeam, Stadium stadium) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.stadium = stadium;
        city = stadium.city;
    }
    
    Game() {
    	filler = true;
    }
}

class Team {
	String name;
	City city;
	ArrayList<Game> schedule = new ArrayList<Game>();
	
	Team (String name, City city) {
		this.name = name;
		this.city = city;
	}
	
	Team () {
		//filler team
	}
	
}

public class Main {
	public static ArrayList<City> dataReader() {
        ArrayList<City> cities = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File("data"));
            int numCities = scanner.nextInt();

            for (int i = 0; i < numCities; i++) {
                String cityName = scanner.next();
                int amountOfTeams = scanner.nextInt();
                
                City city = new City(cityName, amountOfTeams);

                for (int k = 0; k < amountOfTeams; k++) {
                	Team team = new Team(scanner.next(), city);
                	city.teams.add(team);
                }
                
                int numStadia = scanner.nextInt();

                for (int k = 0; k < numStadia; k++) {
                    String name = scanner.next();
                    Stadium stadium = new Stadium(city, name);
                    city.stadiums.add(stadium);
                }

                cities.add(city);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: file not found.");
            System.exit(1);
        }

        System.out.println("INPUT DATA:");
        System.out.println();
        for (City city : cities) {
            System.out.println("City: " + city.name);
            System.out.println("  Teams: ");
            for (Team team : city.teams) {
                System.out.println("    " + team.name);
            }
            System.out.println("  Stadiums: ");
            for (Stadium stadium : city.stadiums) {
                System.out.println("    " + stadium.name);
            }
            System.out.println();
        }
        return cities;
    }
	
	public static void scheduleGenerator(ArrayList<City> cities, int NUMGAMES) {
		
		for (City city : cities) {
    		if (city.name.equals("Waterdown") || city.name.equals("Ancaster") || city.name.equals("StoneyCreek")) {
				city.book();
			}  
    	}
		
        Stadium filler = new Stadium();
        
        ArrayList<Team> teams = new ArrayList<>();
        for (City city : cities) {
        	for (Team team : city.teams) {
        		teams.add(team);
        	}
        }
        
        Team empty = new Team();
        if (teams.size() % 2 == 1) {	
        	teams.add(empty);
        }
        
        int numTeams = teams.size();
        
        for (int round = 1; round <= NUMGAMES; round++) {
        	for (int i = 0; i < numTeams/2; i++) {
        		Team team1 = teams.get(i);
        		Team team2 = teams.get(numTeams - i - 1);
        		
        		Stadium stadium1 = null;
        		Game game;
        		if (team1 != empty && team2 != empty) {
	        		//team1 is the home team
	        		if (team1.city.numStadiumsOpen() < team2.city.numStadiumsOpen()) {
	        			Team temp = team1;
	        			team1 = team2;
	        			team2 = temp;
	        		}
	        		
	        		Random rand = new Random();
	        		int ran = rand.nextInt(10) + 1;
	        		if (ran < 5) {
	        			Team temp = team1;
	        			team1 = team2;
	        			team2 = temp;
	        		}
	        		
	        		for (Stadium stadium : team1.city.stadiums) {
	        			if (!stadium.booked) {
	        				stadium1 = stadium;
	        			}
	        		}
	        		
	        		if (stadium1 == null) {
	        			for (Stadium stadium : team2.city.stadiums) {
		        			if (!stadium.booked) {
		        				stadium1 = stadium;
		        			}
		        		}
	        		}
	        		
	        		if (stadium1 == null) {
	        			System.out.println("Warning: Stadium in third city used.");
	        			//System.exit(1);
	        			game = new Game(team1, team2, filler);
	        		} else {
	        			stadium1.booked = true;
	        			game = new Game(team1, team2, stadium1);
	        		}
        		} else {
        			game = new Game();
        		}
        		
        		team1.schedule.add(game);
        		team2.schedule.add(game);
        	}
        	for (Team team : teams) {
        		if (team.schedule.get(team.schedule.size()-1).stadium == filler) {
        			System.out.println("filler");
        			Stadium stadium1 = null;
        			for (City city : cities) {
        				for (Stadium stadium : city.stadiums) {
        					if (stadium.booked == false) {
        						stadium1 = stadium;
        					}
        				}
        			}
        			if (stadium1 == null) {
        				System.out.println("Error: insufficient stadiums." + round);
        				System.exit(1);
        			}
        			team.schedule.get(team.schedule.size()-1).stadium = stadium1;
        			stadium1.booked = true;
        		}
        	}
        	int count = 0;
        	for (City city : cities) {
	        	count += city.unbook();
	        	if (round <= 4) {	
	        		if (city.name.equals("Waterdown") || city.name.equals("Ancaster") || city.name.equals("StoneyCreek")) {
	    				city.book();
	    			}  
	        	}
        	}
        	//System.out.println(count);
        	
        	ArrayList<Team> teams2 = new ArrayList<>();
        	teams2.add(teams.get(0));
        	for (int i = 2; i < teams.size(); i++) {
        		teams2.add(teams.get(i));
        	}
        	teams2.add(teams.get(1));
        	teams = teams2;
        }
        
        //13 = 129, 6:30
        //15 = 131, 6:30
        //17 = 130, 8:30, 129 in Burlington
        //21 = 131, 8:30, 128 in StoneyCreek
        //above chart only applies to 2023, need to find new numbers for other years
        Year year = Year.of(2023);
        
        System.out.println("SCHEDULE:");
        System.out.println("");
    	for (Team team : teams) {
    		if (team != empty) {
	            System.out.println("Team " + team.name + " schedule:");
	            int round = 1;
	            for (Game game : team.schedule) {
	            	if (!game.filler) {
	            		LocalDate date = year.atDay(129 + ((round-1)*7));
	            		if (17 == 17 && game.stadium.city.name.charAt(0) == "B".charAt(0)) {
	            			date = year.atDay(129 + ((round-1)*7));
	            		}
	            		if (21 == 21 && game.stadium.city.name.charAt(0) == "S".charAt(0)) {
	            			//date = year.atDay(128 + ((round-1)*7));
	            		}
		                System.out.println("    " + round + "," + 
		                		date + ",8:30," +
		                		game.homeTeam.name + "," +
		                        game.awayTeam.name + "," +
		                        game.stadium.name + "," +
		                        game.stadium.city.name); //you can replace this line's variable with "game.stadium.city.name if the program has a runtime error, however then there will be instances where a game happens of two teams playing in a stadium in a city that is not the city of either team. for last case resorts. 
	            	} else {
	            		System.out.println("    " + round + ",No Game.");
	            	}	
	                round++;
	            }
	            System.out.println();
    		}
    	}
    }
	
    public static void main(String[] args) {
    	scheduleGenerator(dataReader(),13);
    }
}