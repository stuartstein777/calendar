package main

import (
	"fmt"
	"os/exec"
)

func publish(repoPath string) {
	cmd := exec.Command("bb", "update.clj", "calendar")

	cmd.Dir = repoPath

	_, err := cmd.Output()

	if err != nil {
		fmt.Printf("Error publishing the repo: %v\n", err)
		fmt.Scanln()
		return
	}
}
