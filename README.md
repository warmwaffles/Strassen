# Strassen's Method

With this version of strassen's method, linked lists are used.

## Why

This started out as a project for my Analysis of Algorithms class. I attempted
Strassen's method with a normal array implementation. It was crude and can be
viewed at ``src/matrix/Matrix.java``. It's not pretty, nor was it intended to be.

Since Java as we all know is OOP I thought the best way to make this algorithm
efficient and fast was to re-use objects rather than throw them away or just
recreating a simple 2-D array.

Thus my project was born.

## How It Works

This I am still trying to hammer out some. I will show some test cases of how to
use it.

## Efficiency...

Yea it is efficient...somewhat. No caching is used with this at all. It is all
loaded into memory and ran there. I am almost positive this would really benefit
if some sort of caching was used.