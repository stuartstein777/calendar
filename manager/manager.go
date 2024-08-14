package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"os"
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

	config := loadConfig()

	events := loadJson(config.EventsLocation)

	for {
		showMenu()
		var input int
		fmt.Scanln(&input)
		if input == 1 {
			events = addEvent(events)
			saveEvents(config.EventsLocation, events)
			publish(config.RepoRoot)
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
	fmt.Print("\033[H\033[2J")
	fmt.Println("1. Add event")
	fmt.Println("2. Edit event")
	fmt.Println("3. Delete event")
	fmt.Println("4. View event")
	fmt.Println("5. Quit")
}

func deleteEvent(events []Event, id int) []Event {

}

func addEvent(events []Event) []Event {
	reader := bufio.NewReader(os.Stdin)
	event := Event{}

	fmt.Print("Description: ")
	event.Description, _ = reader.ReadString('\n')
	event.Description = event.Description[:len(event.Description)-1]

	fmt.Print("Date: ")
	event.Date, _ = reader.ReadString('\n')
	event.Date = event.Date[:len(event.Date)-1]

	fmt.Print("Type: ")
	event.Type, _ = reader.ReadString('\n')
	event.Type = event.Type[:len(event.Type)-1]

	fmt.Print("Location: ")
	event.Location, _ = reader.ReadString('\n')
	event.Location = event.Location[:len(event.Location)-1]

	if len(events) == 0 {
		event.Id = 1
	} else {
		id := 0
		for _, e := range events {
			if e.Id > id {
				id = e.Id
			}
		}
		event.Id = id + 1
		fmt.Printf("%d\n", event.Id)
	}

	events = append(events, event)
	fmt.Scanln()
	return events
}

func filterEvents(events []Event, month int) []Event {
	filteredEvents := []Event{}
	for _, event := range events {
		if event.Date[5:7] == fmt.Sprintf("%02d", month) {
			filteredEvents = append(filteredEvents, event)
		}
	}
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

		filteredEvents := events

		if month >= 0 && month <= 12 {
			if month == 0 {
				filteredEvents = events
			} else {
				filteredEvents = filterEvents(events, month)
			}
		} else {
			fmt.Println("Invalid month")
			continue
		}

		fmt.Println()
		fmt.Printf("%-5s %-40s %-15s %-15s %-30s\n", "Id", "Description", "Date", "Type", "Location")
		fmt.Println("=======================================================================================================")

		for _, event := range filteredEvents {
			fmt.Printf("%-5d %-40s %-15s %-15s %-30s\n", event.Id, event.Description, event.Date, event.Type, event.Location)
		}
		fmt.Println("=======================================================================================================")

		fmt.Printf("<b> for main menu: ")
		var input string
		fmt.Scanln(&input)

		if input == "b" {
			break
		}
	}
}

func loadJson(path string) []Event {

	file, err := os.Open(path)
	if err != nil {
		if err == os.ErrNotExist {
			fmt.Printf("The events.json file does not exist. It should be in %s\n", path)
		} else {
			fmt.Printf("Error reading the events.json file: %v\n", err)
		}
	}

	defer file.Close()
	events := []Event{}
	json.NewDecoder(file).Decode(&events)

	sort.Slice(events, func(i, j int) bool {
		return events[i].Date < events[j].Date
	})
	return events
}

func saveEvents(path string, events []Event) {
	file, err := os.Create(path)
	if err != nil {
		fmt.Printf("Error creating the events.json file: %v\n", err)
	}
	defer file.Close()
	json.NewEncoder(file).Encode(events)
}
