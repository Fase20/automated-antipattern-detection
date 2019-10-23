dtmc

const double c1 = 1.2;
const double rt1 = 150;
const double p1 = 0.995;

const double c2 = 0.6;
const double rt2 = 200;
const double p2 = 0.95;

const double c3 = 1.1;
const double rt3 = 153;
const double p3 = 0.99;

const double c4 = 0.6;
const double rt4 = 200;
const double p4 = 0.95;

const double timeout = 2000;

module FUNDAMENTAL_ANALYSIS

  s : [1..15] init 1;

  [] s=1 -> p1:(s'=5) + (1-p1):(s'=6);
  [] s=2 -> p2:(s'=7) + (1-p2):(s'=8);
  [] s=3 -> p3:(s'=9) + (1-p3):(s'=10);
  [] s=4 -> p4:(s'=11) + (1-p4):(s'=12);

  [] s=5 -> (s'=13); //Service success
  [] s=6 -> (s'=2); //Failure, go to next impl

  [] s=7 -> (s'=13); //Service success
  [] s=8 -> (s'=3); //Failure, go to next impl

  [] s=9 -> (s'=13); //Service success
  [] s=10 -> (s'=4); //Failure, go to next impl

  [] s=11 -> (s'=13); //Service success
  [] s=12 -> (s'=14); //Failure

  [] s=13 -> (s'=15); //success
  [] s=14 -> (s'=15); //failure
  [] s=15 -> (s'=15); //done
endmodule

label "success" = s=13;
label "done" = s=15;

rewards "cost"
  s=1 : c1;
  s=2 : c2;
  s=3 : c3;
  s=4 : c4;
endrewards

rewards "rt"
  s=5 : rt1;
  s=7 : rt2;
  s=9 : rt3;
  s=11 : rt4;
  s=6|s=8|s=10|s=12 : timeout;
endrewards