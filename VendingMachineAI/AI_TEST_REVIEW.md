The overall quality of the generated tests was relatively high, the AI did a good job of doing really thorough parameterized tests. The AI didn't do a great job of keeping it simple with the tests, since all negative numbers are treated the same.

The AI-generated test suit performed slightly better than my test suit, mostly because I was doing a lot of copy pasting and I didn't change a value from E to A and didn't notice that. 
The test quality was pretty similar overall.
Coverage-guided promting did not improve the generated suite, it only started covering things that were technically in the design docs by exclusion, thus making them allowed, i.e. null names and null items being inserted. 
The AI did a lot of redundant assertions, but not any incorrect ones. I can recognize redundant assertions from their parametric nature, and how they test several things that will all be rejected in the same place. 
This suggests that AI is pretty good at generating complete software tests, but isn't great at producing the minimum amount of tests. 
