/*
 * onetimepad.cpp
 *
 *  Created on: Dec 23, 2018
 *      Author: oscarsplitfire
 */

#include <iostream>
#include <stdio.h>
#include <string>
#include <vector>

#include <algorithm>

#define KEY_LEN 500


std::string &encrypt(std::vector<int> &key, std::string &msg) {
	for (int i = 0; i < msg.length(); i++) {
		msg[i] -= 65;
		msg[i] += key[i];
		msg[i] %= 26;
		msg[i] += 65;
	}
	return msg;
}

std::string &decrypt(std::vector<int> &key, std::string &msg) {
	for (int i = 0; i < msg.length(); i++) {
		msg[i] -= 65;
		if ((int) msg[i] - (int) key[i] < 0) {
			msg[i] += 26;
		}
		msg[i] -= key[i];
		msg[i] += 65;
	}
	return msg;
}


int main(int argc, char *argv[]) {

	puts("");

	std::vector<int> *key = nullptr;
	std::string *input = nullptr;
	bool modeenc = true;

	if (argc == 1) {
		puts("-e: encrypt mode");
        puts("-d: decrypt mode");
        puts("-g: generate key\n");
        puts("-k: specify key");
        puts("-i: specify input");
        return 0;
	}

	switch (argv[1][1]) {

	case 'e':
		modeenc = true;
		break;

	case 'd':
		modeenc = false;
		break;

	case 'g':
		puts("https://www.random.org/strings/?num=25&len=20&upperalpha=on&unique=on&format=plain&rnd=new");
		return 0;

	default:
		std::cerr << "Invalid mode specifier\n";
		return 0;

	}

	for (int i = 2; i < argc; i++) {

		switch (argv[i][1]){

		case 'k':
		{
			char *in = argv[++i];
			int len = strlen(in);
			if (len != KEY_LEN) {
				fprintf(stderr, "Error: Invalid Key Length (%d), should be %d\n", len, KEY_LEN);
				return 0;
			}
			key = new std::vector<int>(KEY_LEN);
			for (int j = 0; j < KEY_LEN; j++) {
				(*key)[j] = in[j] - 65;
			}
			break;
		}

		case 'i':
			input = new std::string(argv[++i]);
			std::transform(input->begin(), input->end(), input->begin(), ::toupper);

			if (input->length() > KEY_LEN) {
				fprintf(stderr, "Error: Input too long (%lu), maximum %d\n", input->length(), KEY_LEN);
				return 0;
			}
			break;

		default:
			fprintf(stderr, "Invalid argument \" %s \"\n", argv[i]);
			return 0;
			break;

		}

	}

	// prompt input
	if (input == nullptr) {
        input = new std::string();
		puts(modeenc ? "Enter plaintext: " : "Enter ciphertext: ");
		puts("");
		std::getline(std::cin, *input);
        std::transform(input->begin(), input->end(), input->begin(), ::toupper);
		if (input->length() > KEY_LEN) {
			fprintf(stderr, "Error: Input too long (%lu), maximum %d\n", input->length(), KEY_LEN);
			return 0;
		}
	}

	// prompt key
	if (key == nullptr) {
		puts("Enter Key: \n");
		key = new std::vector<int>(KEY_LEN);
		std::string chars = "";
		std::string cat;
		while (std::cin >> cat) {
            chars.append(cat);
		}

		if (chars.length() != KEY_LEN) {
			fprintf(stderr, "Error: Invalid Key Length (%lu), should be %d\n", chars.length(), KEY_LEN);
			return 0;
		}

		for (int i = 0; i < chars.length(); i++) {
			(*key)[i] = chars.at(i) - 65;
		}
	}

	if (modeenc) {
		puts("Encrypted: \n\n");
		std::cout << encrypt(*key, *input) << "\n";
	}
	else {
		puts("Decrypted: \n\n");
		std::cout << decrypt(*key, *input) << "\n";
	}

	return 0;
}



