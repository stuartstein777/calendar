package main

import (
	"encoding/json"
	"fmt"
	"os"
)

type Config struct {
	EventsLocation string
	RepoRoot       string
}

var configLocation = "/usr/local/etc/calendar/config.json"

func loadConfig() Config {

	file, err := os.Open(configLocation)
	if err != nil {
		if err == os.ErrNotExist {
			fmt.Printf("The config file does not exist. It should be in %s\n", configLocation)
		} else {
			fmt.Printf("Error reading the config file: %v\n", err)
		}
	}
	defer file.Close()
	config := Config{}
	json.NewDecoder(file).Decode(&config)
	return config
}
