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
	Name        string
	Description string
	Date        string
	Type        string
	Location    string
}

var config = Config{}

func main() {
	fmt.Println("Calendar manager")

	config = loadConfig()

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
			events = editEvent(events)
		} else if input == 3 {
			events = deleteEvent(events)
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

func printEvents(events []Event) {

	fmt.Println()
	fmt.Printf("%-5s %-20s %-40s %-15s %-15s %-30s\n", "Id", "Name", "Description", "Date", "Type", "Location")
	fmt.Println("=============================================================================================================")

	for _, event := range events {
		printEvent(event)
	}
	fmt.Println("=============================================================================================================")
}

func findEvent(events []Event, id int) Event {
	for _, event := range events {
		if event.Id == id {
			return event
		}
	}
	return Event{}
}

func printEvent(event Event) {
	fmt.Printf("%-5d %-20s %-40s %-15s %-15s %-30s\n", event.Id, event.Name, event.Description, event.Date, event.Type, event.Location)
}

func removeEvent(events []Event, id int) []Event {
	newEvents := []Event{}
	for _, event := range events {
		if event.Id != id {
			newEvents = append(newEvents, event)
		}
	}
	return newEvents
}

func getInput(ifEmpty string) string {
	reader := bufio.NewReader(os.Stdin)
	input, _ := reader.ReadString('\n')
	trimmed := input[:len(input)-1]
	if trimmed == "" {
		return ifEmpty
	}
	return trimmed
}

func editEvent(events []Event) []Event {
	for {
		fmt.Print("\033[H\033[2J")
		fmt.Print("Month ?")

		var month int
		fmt.Scanln(&month)

		if month >= 0 && month <= 12 {
			filteredEvents := filterEvents(events, month)
			printEvents(filteredEvents)
			fmt.Printf("Id of the event to delete: ")
			var id int
			fmt.Scanln(&id)
			eventToEdit := findEvent(filteredEvents, id)

			fmt.Printf("%-16s %s\n", "Name:", eventToEdit.Name)
			fmt.Printf("%-17s", "New Name:")
			eventToEdit.Name = getInput(eventToEdit.Name)

			fmt.Printf("%-16s %s\n", "Description:", eventToEdit.Description)
			fmt.Printf("%-17s", "New description:")
			eventToEdit.Description = getInput(eventToEdit.Description)

			fmt.Printf("%-16s %s\n", "Date:", eventToEdit.Date)
			fmt.Printf("%-17s", "New Date:")
			eventToEdit.Date = getInput(eventToEdit.Date)

			fmt.Printf("%-16s %s\n", "Type:", eventToEdit.Type)
			fmt.Printf("%-17s", "New Type:")
			eventToEdit.Type = getInput(eventToEdit.Type)

			fmt.Printf("%-16s %s\n", "Location:", eventToEdit.Location)
			fmt.Printf("%-17s", "New Location:")
			eventToEdit.Location = getInput(eventToEdit.Location)

			fmt.Printf("New Event\n")
			fmt.Printf("%-5s %-20s %-40s %-15s %-15s %-30s\n", "Id", "Name", "Description", "Date", "Type", "Location")
			printEvent(eventToEdit)
			fmt.Printf("Save changes? (y/n): ")
			var input string
			fmt.Scanln(&input)
			if input == "y" {
				for i, event := range events {
					if event.Id == id {
						events[i] = eventToEdit
						break
					}
				}
				fmt.Printf("Saving changes\n")
				saveEvents(config.EventsLocation, events)
				fmt.Printf("Publishing\n")
				publish(config.RepoRoot)
				return events
			} else {
				break
			}
		}
	}

	return events
}

func deleteEvent(events []Event) []Event {
	for {
		fmt.Print("\033[H\033[2J")
		fmt.Print("Month ?")

		var month int
		fmt.Scanln(&month)

		filteredEvents := events

		if month >= 0 && month <= 12 {
			if month == 0 {
				filteredEvents = events
			} else {
				filteredEvents = filterEvents(events, month)

				printEvents(filteredEvents)

				fmt.Printf("Id of the event to delete: ")
				var id int
				fmt.Scanln(&id)
				printEvent(findEvent(filteredEvents, id))
				fmt.Printf("Delete this event? (y/n): ")
				var input string
				fmt.Scanln(&input)
				if input == "y" {
					events = removeEvent(events, id)
					fmt.Printf("Saving events")
					saveEvents(config.EventsLocation, events)
					fmt.Printf("Publishing\n")
					publish(config.RepoRoot)
					return events
				} else {
					break
				}
			}
		}
	}
	return events
}

func addEvent(events []Event) []Event {
	reader := bufio.NewReader(os.Stdin)
	event := Event{}

	fmt.Print("Name: ")
	event.Name, _ = reader.ReadString('\n')
	event.Name = event.Name[:len(event.Name)-1]

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
	}

	events = append(events, event)
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

		printEvents(filteredEvents)

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
