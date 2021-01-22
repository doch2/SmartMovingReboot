// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package net.smart.moving;

import java.io.PrintStream;

public class SMHandsClimbing {
	public static final int MiddleGrab = 2;
	public static final int UpGrab = 1;
	public static final int NoGrab = 0;

	public static SMHandsClimbing None = new SMHandsClimbing(-3);
	public static SMHandsClimbing Sink = new SMHandsClimbing(-2);
	public static SMHandsClimbing TopHold = new SMHandsClimbing(-1);
	public static SMHandsClimbing BottomHold = new SMHandsClimbing(0);
	public static SMHandsClimbing Up = new SMHandsClimbing(1);
	public static SMHandsClimbing FastUp = new SMHandsClimbing(2);

	private int _value;

	private SMHandsClimbing(int value) {
		_value = value;
	}

	public boolean IsRelevant() {
		return _value > None._value;
	}

	public boolean IsUp() {
		return this == Up || this == FastUp;
	}

	public SMHandsClimbing ToUp() {
		if (this == BottomHold)
			return Up;
		return this;
	}

	public SMHandsClimbing ToDown() {
		if (this == TopHold)
			return Sink;
		return this;
	}

	public SMHandsClimbing max(SMHandsClimbing other, SMClimbGap inout_thisClimbGap, SMClimbGap otherClimbGap) {
		if (!otherClimbGap.SkipGaps) {
			inout_thisClimbGap.CanStand |= otherClimbGap.CanStand;
			inout_thisClimbGap.MustCrawl |= otherClimbGap.MustCrawl;
		}
		if (_value < other._value) {
			inout_thisClimbGap.Block = otherClimbGap.Block;
			inout_thisClimbGap.Meta = otherClimbGap.Meta;
			inout_thisClimbGap.Direction = otherClimbGap.Direction;
		}
		return get(Math.max(_value, other._value));
	}

	@Override
	public String toString() {
		if (_value <= None._value)
			return "None";
		if (_value == Sink._value)
			return "Sink";
		if (_value == BottomHold._value)
			return "BottomHold";
		if (_value == TopHold._value)
			return "TopHold";
		if (_value == Up._value)
			return "Up";
		return "FastUp";
	}

	public void print(String name) {
		PrintStream stream = System.err;
		if (name != null)
			stream.print(name + " = ");
		stream.println(this);
	}

	private static SMHandsClimbing get(int value) {
		if (value <= None._value)
			return None;
		if (value == Sink._value)
			return Sink;
		if (value == BottomHold._value)
			return BottomHold;
		if (value == TopHold._value)
			return TopHold;
		if (value == Up._value)
			return Up;
		return FastUp;
	}
}
