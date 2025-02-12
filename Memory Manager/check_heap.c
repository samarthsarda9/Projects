
#include "umalloc.h"
#include <stdio.h>

//Place any variables needed here from umalloc.c as an extern.
extern mem_block_header_t *free_head;

/*
 * check_heap -  used to check that the heap is still in a consistent state.
 *
 * Should return 0 if the heap is still consistent, otherwise return a non-zero
 * return code.
 */
int check_heap() {
    // Example heap check:
    // Check that all blocks in the free list are marked free.
    mem_block_header_t *cur = free_head;
    // Checks if block is not NULL
    if (cur == NULL) {
        return -1;
    } else {
        // Checks if block is allocated
        if (is_allocated(cur)) {
            return -2;
        }
        // Checks for alignment
        if ((unsigned long) cur % 16 != 0) {
            return -3;
        }
        // Go through free list
        while (cur->next != NULL) {
            // Checks block for allocation
            if (is_allocated(cur)) {
                return -2;
            }
            // Checks if a block is after in memory to its next memory block
            if ((unsigned long) cur > (unsigned long) cur->next) {
                return -4;
            }
            // Checks for alignment
            if ((unsigned long) cur->next % 16 != 0) {
                return -3;
            }
            // Checks for overlap between two blocks
            if ((unsigned long) cur + (cur->block_metadata + 16) > (unsigned long) cur->next) {
                return -5;
            }
            cur = cur->next;
        }
    }
    return 0;
}
