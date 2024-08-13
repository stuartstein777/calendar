package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"sort"
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
			viewEvents(events)
		} else if input == 5 {
			fmt.Print("\033[H\033[2J")
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

func filterEvents(events []Event, month int) []Event {
	filteredEvents := []Event{}
	for _, event := range events {
		if event.Date[5:7] == fmt.Sprintf("%02d", month) {
			filteredEvents = append(filteredEvents, event)
		}
	}
	// order the events by day
	sort.Slice(filteredEvents, func(i, j int) bool {
		return filteredEvents[i].Date < filteredEvents[j].Date
	})
	return filteredEvents
}

func viewEvents(events []Event) {
	for {
		fmt.Print("\033[H\033[2J")
		fmt.Print("Month ? ")
		var month int
		fmt.Scanln(&month)

		filteredEvents := filterEvents(events, month)

		fmt.Println()
		fmt.Printf("%-5s %-40s %-15s %-15s %-30s\n", "Id", "Description", "Date", "Type", "Location")
		fmt.Println("=======================================================================================================")

		for _, event := range filteredEvents {
			fmt.Printf("%-5d %-40s %-15s %-15s %-30s\n", event.Id, event.Description, event.Date, event.Type, event.Location)
		}
		fmt.Println("=======================================================================================================")

		fmt.Printf("b to go back to main menu: ")
		var input string
		fmt.Scanln(&input)

		if input == "b" {
			break
		}
	}
}

func loadJson() []Event {
	// read the json file from repo url
	url := "https://stuartstein777.github.io/calendar/events.json"

	resp, err := http.Get(url)
	if err != nil {
		fmt.Println("Error reading the json file")
	}
	defer resp.Body.Close()

	event := []Event{}

	json.NewDecoder(resp.Body).Decode(&event)
	return event
}
