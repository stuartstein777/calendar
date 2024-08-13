package main

import (
	"encoding/json"
	"fmt"
	"net/http"
)

type Event struct {
	Id          int
	Description string
	Date        string
	Type        string
	Location    string
}

func main() {
	fmt.Println("Calendar manager")

	events := loadJson()

	fmt.Printf("Description: %s\n", events[0].Description)

	for {
		// /fmt.Print("\033[H\033[2J")
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

func loadJson() []Event {
	// read the json file from repo url
	url := "https://github.com/stuartstein777/calendar/blob/main/events.json"

	resp, err := http.Get(url)
	if err != nil {
		fmt.Println("Error reading the json file")
	}
	defer resp.Body.Close()

	event := []Event{}
	json.NewDecoder(resp.Body).Decode(&event)
	return event

}
