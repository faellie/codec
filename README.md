# codec
Simple hamming codec but with additionaly dup codec for each bits to enhance the code. i.e each originaly data bit 1 is represented by 111 and 0 is represent by 000.
The assumption is 1 can be mistaken as 0 but 0 is always 0. there for for hte dup coded 111 101 100 110 011 001 etc are all treated as 1 and only 000 is treated as 0. 
