#ifndef SERIALJSON_H
#define SERIALJSON_H

#include "dfunc/fdex.h"
#include "jlib/jsonlib.h"

/**
 * Declare the functions for each item
 */

int led1_function(int argc, int *argv);

/**
 * Declare the items with their names and functions
 * The items are used to map the JSON keys to their corresponding functions.
 */

Item ITEMS[] = {
    {"led1", led1_function},
    {NULL, NULL} // Sentinel value to mark the end of the array
};

#endif // SERIALJSON_H
