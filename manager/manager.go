package main

import (
	"fmt"
)

type Event struct {
	Id          int
	Description string
	StartDate   string
	Type        string
	Location    string
}

func main() {
	fmt.Println("Calendar manager")

	// read the json file from repo url

	// parse the json file

	// show the menu
	for {
		fmt.Print("\033[H\033[2J")
		showMenu()
		var input int
		fmt.Scanln(&input)
		if input == 1 {
			fmt.Println("Add event")
		} else if input == 2 {
			fmt.Println("Edit event")
		} else if input == 3 {
			fmt.Println("Delete event")
		} else if input == 4 {
			fmt.Println("View event")
		} else if input == 5 {
			fmt.Println("Closing")
			break
		} else {
			fmt.Println("Invalid option")
		}
	}
}

func showMenu() {
	fmt.Println("1. Add event")
	fmt.Println("2. Edit event")
	fmt.Println("3. Delete event")
	fmt.Println("4. View event")
	fmt.Println("5. Quit")
}
